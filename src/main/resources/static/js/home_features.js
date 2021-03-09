var allFeatures = (function () {

    var $cache = {};

    var init = function () {
        initCache();
        initView();
        bindEvents();
    };

    var initCache = function () {

        $cache.body = $("body");
        $cache.workflowsEnableFormEntries = $("#entries");
        $cache.generateClustersForm = $("#wokflows_enable_form");
        $cache.addNewWFForm = $("#add_wf_form");
        $cache.locationPathsForm = $("#location_paths_form");
        $cache.locationPathsList = $("#location_paths_list");
        $cache.clustersPanelList = $("#clusters_panel_list");

    };

    var bindEvents = function () {

        $cache.body.on("change", ".wfchb", function (event) {

            var enabled = this.checked;

            $.ajax({
                method: "POST",
                url: "http://localhost:8080/wffile/enable?id=" + this.id + "&enabled=" + enabled,
                processData: false
            }).done(function (response) {

                console.log(response);

            }).fail(function (msg) {
                console.log("Failed");
                console.log(msg);
                this.checked = !enabled;
            });

        });

        $cache.body.on("click", ".collapsible", function (event) {

            this.classList.toggle("active");
            var content = this.nextElementSibling;
            if (content.style.maxHeight) {
                content.style.maxHeight = null;
            } else {
                content.style.maxHeight = content.scrollHeight + "px";
            }

        });

        $cache.body.on("click", "button.remove_wf", function (event) {
            event.preventDefault();
            console.log(this);
            console.log($(this).data("id"));

            $.ajax({
                method: "DELETE",
                url: "http://localhost:8080/wffile/remove?id=" + $(this).data("id"),
                processData: false
            }).done(function (response) {

                alert("WF Deleted");
                updateEnabledWF();

            }).fail(function (msg) {
                console.log("Failed");
                console.log(msg);
                alert("Failed: " + msg);
            });

        });


        $cache.generateClustersForm.on("submit", function (event) {

            event.preventDefault();

            $.ajax({
                method: "POST",
                url: "http://localhost:8080/cluster/create",
                processData: false
            }).done(function (wffiles) {

                updateClustersView();
                alert("Clusters generated");


            }).fail(function (msg) {
                console.log("Failed");
                console.log(msg);
                alert("Failed: " + msg);
            });

        });

        $cache.addNewWFForm.on("submit", function (event) {
            event.preventDefault();
            var data = new FormData(this);

            var name = data.get("wf_new_name").trim();
            var path = data.get("wf_new_path").trim();
            var enabled = data.get("wf_new_enabled") === "on";

            if (name && path) {
                console.log(name);
                console.log(path);
                console.log(enabled);

                var payload = {
                    "id": name,
                    "location": path,
                    "enabled": enabled
                };

                $.ajax({
                    method: "POST",
                    url: "http://localhost:8080/wffile",
                    data: JSON.stringify(payload),
                    processData: false,
                    contentType: "application/json"
                }).done(function (msg) {

                    alert("Workflow " + name + " saved correctly");
                    console.log(msg);
                    updateEnabledWF();

                }).fail(function (msg) {
                    console.log("Failed");
                    console.log(msg);
                });

            }

        });

        $cache.locationPathsForm.on("submit", function (event) {

            event.preventDefault();

            var data = new FormData(this);
            var payload = [];
            console.log(data);

            $("#location_paths_form .wf_path").each(function (index) {

                var tmp = {
                    "id": $(this).attr("name"),
                    "location": $(this).val()
                };

                payload.push(tmp);

            });

            $.ajax({
                method: "POST",
                url: "http://localhost:8080/wffile/pathupdate",
                data: JSON.stringify(payload),
                processData: false,
                contentType: "application/json"
            }).done(function (msg) {

                alert("Workflows paths updated");
                console.log(msg);
                updateEnabledWF();

            }).fail(function (msg) {
                console.log("Failed");
                console.log(msg);
            });

        });

    };

    var initView = function () {
        updateEnabledWF();
        updateClustersView();
    };

    var updateClustersView = function () {

        $.ajax({
            method: "GET",
            url: "http://localhost:8080/cluster/all",
            processData: false
        }).done(function (clustersResponse) {

            $(".cluster_li").remove();

            var ll = clustersResponse.length - 1;

            for (it in clustersResponse) {

                var clusterObj = clustersResponse[ll - it];
                var nodeDefinitions = clusterObj.nodeDefinitions;

                var rulesList = nodeDefinitions.reduce(function (accumulator, nodeDef) {
                    return accumulator + "<li>" + nodeDef.label + "</li>"
                }, "");

                var element = "<li class=\"cluster_li\">" +
                    "<a href=\"http://localhost:8080/clusterview?clusterId=" +
                    clusterObj.id +
                    "\" target=\"_blank\">" +
                    "<h2>Cluster " +
                    clusterObj.id +
                    "</h2>" +
                    "</a>" +

                    "<button class=\"collapsible\">Rulesets (" +
                    nodeDefinitions.length +
                    ")</button>" +
                    "<div class=\"content\">" +
                    "<ul>" +
                    rulesList.valueOf() +
                    "</ul>\n" +
                    "</div>" +
                    "</li>";

                $cache.clustersPanelList.append(element);

            }

        }).fail(function (msg) {
            console.log("Failed");
            console.log(msg);
        });

    };

    var updateEnabledWF = function () {

        $.ajax({
            method: "GET",
            url: "http://localhost:8080/wffile/all",
            processData: false
        }).done(function (wffiles) {

            $("#wokflows_enable_form #entries .form_section").remove();
            $("#location_paths_form #location_paths_list .form_section").remove();

            for (it in wffiles) {

                $cache.workflowsEnableFormEntries.append("<div class=\"form_section\">\n" +
                    "<input class=\"wfchb\" type=\"checkbox\" id=\"" +
                    wffiles[it].id +
                    "\" name=\"" +
                    wffiles[it].id +
                    "\" value=\"" +
                    wffiles[it].id + "\" " +
                    ((wffiles[it].enabled) ? "checked" : "") +
                    ">\n" +
                    "<label for=\"" +
                    wffiles[it].id +
                    "\">" +
                    wffiles[it].id +
                    "</label>\n" +
                    "</div>");

                $cache.locationPathsList.append("<div class=\"form_section\">" +
                    "<label for=\"" +
                    wffiles[it].id +
                    "-loc\">" +
                    wffiles[it].id +
                    ":</label>" +
                    "<br>" +
                    "<input class=\"wf_path\" type=\"text\" id=\"" +
                    wffiles[it].id +
                    "-loc\" name=\"" +
                    wffiles[it].id +
                    "\"" +
                    "value=\"" +
                    wffiles[it].location +
                    "\">" +
                    "<button data-id=\"" +
                    wffiles[it].id +
                    "\" class=\"remove_wf\">Remove</button>" +
                    "</div>");

            }

        }).fail(function (msg) {
            console.log("Failed");
            console.log(msg);
        });
    };

    return {
        allinit: init
    };

})();

$(document).on('ready', allFeatures.allinit);
