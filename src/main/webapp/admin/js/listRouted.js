/*
 * Javascript file attached to the listRouted.xhtml page. This invokes the Web Sockets functionality
 * to populate the live Cargo Tracker widget on the dashboard
 */

(function() {

    var ws = new WebSocket("ws://localhost:8080/cargo-tracker/tracking");
    ws.onopen = function(event) {
        onConnect(event)
    };
    ws.onmessage = function(event) {
        onMessage(event)
    };

    onConnect = function(event) {
    }

    onMessage = function(event) {
        populateListRouted(event);
    }

    populateListRouted = function(event) {

        var jsonObject = JSON.parse(event.data);
        var table = document.getElementById("listRoutedTab");

        for (var count = 1, row; row = table.rows[count]; count++) {
            if (row.id === jsonObject.trackingId) {
                row.cells[1].innerHTML = jsonObject.origin;
                row.cells[2].innerHTML = jsonObject.destination;
                row.cells[3].innerHTML = jsonObject.lastKnownLocation;
                row.cells[4].innerHTML = jsonObject.transportStatus;
                break;
            }
        }
    }

})();

