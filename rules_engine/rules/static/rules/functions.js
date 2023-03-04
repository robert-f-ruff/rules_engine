function getParameters(action) {
    if (action.value != "") {
        //Alert user of data retrieval
        let id = action.id.split("-")[1];
        let parameter_area = document.getElementById("action_parameter_" + id + "_ActionParameterForm");
        let message = document.createElement("p");
        message.innerText = "Retrieving list of parameters..."
        message.id = "server_message_" + id;
        parameter_area.appendChild(message)
        //Make the call to retrieve the list of parameters
        let new_path = ""
        if (window.location.pathname.indexOf("/add/") > 0) {
            new_path = window.location.pathname.replace("/add/","/parameters/" + action.value + "/");
        } else {
            let stop = window.location.pathname.search(/[/]\d+[/]/);
            if (stop > 0) {
                new_path = window.location.pathname.slice(0, stop) + "/parameters/" + action.value + "/";
            }
        }
        new_path = new_path + "?ruleaction_set-id=" + id;
        new_path = new_path + "&ruleaction-id=" + document.getElementById("id_ruleactions_set-" + id + "-id").value;
        parseParameters(window.location.origin + new_path, id);
    }
}

async function parseParameters(location, action_number) {
    let response = await fetch(location);
    let form_code = await response.json();
    let parameter_area = document.getElementById("action_parameter_" + action_number + "_ActionParameterForm");
    let action_record_id = document.getElementById("id_ruleactions_set-" + action_number + "-id").value;
    if (action_record_id != "") {
        let parameter_count = document.getElementById("id_param" + action_record_id + "-TOTAL_FORMS").value;
        for (var i = 0; i < parameter_count; i++) {
            let parameter_control = document.getElementById("id_param" + action_record_id + "-" + i + "-parameter_value");
            let label_control = parameter_control.previousElementSibling;
            let delete_control = document.getElementById("id_param" + action_record_id + "-" + i + "-DELETE")
            if (form_code.parameter_form != "") {
                label_control.setAttribute("class", "d-none");
                parameter_control.setAttribute("class", "d-none");
                delete_control.value = "True"
            }
            else {
                label_control.setAttribute("class", "");
                parameter_control.setAttribute("class", "");
                delete_control.value = ""
            }
        }
        document.getElementById("server_message_" + action_number).remove();
    }
    parameter_area.innerHTML = form_code.parameter_form;
}

function validateForm() {
    if (validateCriteria() == false) {
        return false;
    }
    return validateActions();
}

function validateCriteria() {
    let criteria_boxes = document.getElementById("id_criteria").getElementsByTagName("input");
    user_checked_one = false;
    for (number = 0; number < criteria_boxes.length; number++) {
        if (criteria_boxes[number].checked) {
            user_checked_one = true;
        }
    }
    if (user_checked_one == false) {
        alert("At least one criterion is required");
        return false;
    }
    return true;
}

function validateActions() {
    let form_count = parseInt(document.getElementById("id_ruleactions_set-TOTAL_FORMS").value)
    for (var number = 0; number < form_count; number++) {
        if (document.getElementById("id_ruleactions_set-" + number + "-DELETE").checked) {
            continue;
        }
        let this_sequence = document.getElementById("id_ruleactions_set-" + number + "-action_number").value;
        let action = document.getElementById("id_ruleactions_set-" + number + "-action").value;
        if (number == 0 && this_sequence != 1) {
            if (action == "") {
                alert("At least one action is required");
                return false;
            }
            alert("Execution sequence #1 should be 1 not " + this_sequence);
            return false;
        }
        else if (number > 0) {
            let last_sequence;
            //Find the first previous sequence not marked for deletion
            for (var last_number = number - 1; last_number >= 0; last_number--) {
                if (! document.getElementById("id_ruleactions_set-" + last_number + "-DELETE").checked) {
                    last_sequence = document.getElementById("id_ruleactions_set-" + last_number + "-action_number").value;
                    break;
                }
            }
            if (typeof last_sequence !== "undefined") {
                if (number == (form_count - 1) && this_sequence == "") {
                    if (action != "") {
                        alert("Execution sequence #" + (number + 1) + " should be " + (parseInt(last_sequence) + 1) + " not empty");
                        return false;
                    }
                    return true;
                }
                else if (this_sequence != (parseInt(last_sequence) + 1)) {
                    alert("Execution sequence #" + (number + 1) + " should be " + (parseInt(last_sequence) + 1) + " not " + this_sequence);
                    return false;
                }
            }
            else if (this_sequence != 1) {
                alert("Execution sequence #" + (number + 1) + " should be 1 not " + this_sequence);
                return false;
            }
        }
        if (action == "" && this_sequence != "") {
            alert("Execution sequence #" + (number + 1) + " must have an action")
            return false;
        }
    }
    return true;
}