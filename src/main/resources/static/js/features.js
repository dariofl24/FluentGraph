var allFeatures = (function () {

    var $cache = {};

    var init = function () {
        initCache();
        bindEvents();
    };

    var initCache = function () {

        $cache.searchBox = $("#searchBox");

        $cache.options = {
            interaction: {
                hideEdgesOnDrag: true,
                hideEdgesOnZoom: true,
                hover: true,
                hoverConnectedEdges: true
            },
            physics: {
                hierarchicalRepulsion: {
                    avoidOverlap: 1
                }
            },
            edges: {
                selectionWidth: 2.5,
                length: 250,
                arrows: 'to',
                font: '10px arial #ff0000',
                scaling: {
                    label: true
                },
                shadow: true,
                smooth: true
            }
        };

        var clusterId = $("#clusterId").text();

        getClusterInfo(clusterId);

        $cache.container = $("#mynetwork")[0];
        $cache.rulesList = $("#rules_list");
        $cache.codeBlock = $("#rule_code");
    };

    var getClusterInfo = function (clusterId) {

        $.ajax({
            method: "GET",
            url: "/cluster?id=" + clusterId,
            processData: false
        }).done(function (cluster) {

            console.log(cluster);

            $cache.cluster = cluster;

            $cache.data = {
                nodes: new vis.DataSet($cache.cluster.nodeDefinitions),
                edges: new vis.DataSet($cache.cluster.relations)
            };

            $cache.nodesIndex = {};

            var it;
            for (it in $cache.cluster.contents) {

                $cache.nodesIndex[$cache.cluster.contents[it].id] = $cache.cluster.contents[it].content;

            }

            drawNetwork();

        }).fail(function (msg) {
            console.log("Failed");
            console.log(msg);
            alert(msg.responseJSON.message);
        });

    };

    var drawNetwork = function () {

        var fLen = $cache.cluster.nodeDefinitions.length;

        for (i = 0; i < fLen; i++) {
            $cache.rulesList.append("<li><a class='ruleset'"
                + " data-nodeid=" +
                $cache.cluster.nodeDefinitions[i].id +
                " data-name=" +
                $cache.cluster.nodeDefinitions[i].label.toLowerCase() +
                ">"
                + $cache.cluster.nodeDefinitions[i].list_label
                + "</a></li>");
        }

        $cache.network = new vis.Network($cache.container, $cache.data, $cache.options);

        $cache.network.on('click', function (properties) {

            if (properties.nodes.length > 0) {

                var nodeId = properties.nodes[0];

                printCodeBlock(nodeId);
            }
        });
    };

    var bindEvents = function () {

        $cache.searchBox.keyup(function () {
            var value = $(this).val().toLowerCase();
            console.log(value);

            var listItems = $("a.ruleset");
            var arrayLength = listItems.length;

            for (var i = 0; i < arrayLength; i++) {

                var item = $(listItems[i]);
                var ruleName = item.data("name");

                if (ruleName.includes(value)) {
                    item.parent("li").show();
                } else {
                    item.parent("li").hide();
                }

            }

        });

        $("body").on("click", ".ruleset", function (event) {
            var nodeId = $(this).data("nodeid");

            focusNetworkNode(nodeId, true);

        });

    };

    var focusNetworkNode = function (nodeId, doAnimation) {
        $cache.network.focus(nodeId, {
            scale: 2.6,
            locked: true,
            animation: doAnimation
        });

        $cache.network.selectNodes([nodeId], [true]);

        printCodeBlock(nodeId);
    };

    var printCodeBlock = function (nodeId) {

        console.log("printCodeBlock");

        var rule = $cache.nodesIndex[nodeId];

        console.log(rule);

        if (rule) {

            //var codeText = JSON.stringify(rule, null, 2);

            $cache.codeBlock.text(rule);

            console.log(hljs);

            hljs.highlightBlock($("pre code")[0]);
        } else {
            $cache.codeBlock.text('');
        }
    };

    return {
        allinit: init
    };

})();

$(document).on('ready', allFeatures.allinit);