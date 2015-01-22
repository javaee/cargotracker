var xhr;

function createRequest() {
    xhr = new XMLHttpRequest();
}

function logIncident() {

    createRequest();
    var form = document.forms[0];

    //Construct the JSON data
    var data = {};
    for (var i = 0, ii = form.length; i < ii; ++i) {
        var input = form[i];
        if (input.name) {
            if (input.value && input.value.trim().length) {
                data[input.name] = input.value;
            }
        }
    }


    xhr.open("POST", "http://localhost:8080/cargo-tracker/rest/handling/reports", true);
    xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
    xhr.send(JSON.stringify(data));
    xhr.onreadystatechange = update;
}

function update() {
    if (xhr.readyState == 4) {
        if ((xhr.status == 204) || (xhr.status == 1223)) {
            alert("Event registered");
            var form = document.forms[0];
            for (var i = 0, ii = form.length; i < ii; ++i) {
                var input = form[i];
                if (input.name) {
                    input.value = "";
                }
            }

        } else {
            alert("Registration failed");
        }
    }
}