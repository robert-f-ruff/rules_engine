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
            let help_text = parameter_control.nextElementSibling;
            let delete_control = document.getElementById("id_param" + action_record_id + "-" + i + "-DELETE");
            if (form_code.parameter_form != "") {
                hideControl(label_control);
                hideControl(parameter_control);
                hideControl(help_text);
                delete_control.value = "True"
            }
            else {
                showControl(label_control);
                showControl(parameter_control);
                showControl(help_text);
                delete_control.value = ""
            }
        }
        document.getElementById("server_message_" + action_number).remove();
    }
    parameter_area.innerHTML = form_code.parameter_form;
}

function hideControl(element) {
    let element_class = element.getAttribute("class");
    element_class = element_class + " d-none";
    element.setAttribute("class", element_class);
}

function showControl(element) {
    let element_class = element.getAttribute("class").replace(/ d[-]none/, "");
    element.setAttribute("class", element_class)
}

function validateForm() {
    if (validateCriteria() == false) {
        return false;
    }
    return validateActionNumbers();
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

function validateActionNumbers() {
    let form_count = parseInt(document.getElementById("id_ruleactions_set-TOTAL_FORMS").value);
    const numbers = [];
    for (var number = 0; number < form_count; number++) {
        let delete_control = document.getElementById("id_ruleactions_set-" + number + "-DELETE");
        if (delete_control != null) {
            if (delete_control.checked) {
                continue;
            }
        }
        let this_sequence = document.getElementById("id_ruleactions_set-" + number + "-action_number").value;
        if (! numbers.includes(this_sequence)) {
            numbers.push(this_sequence);
        }
        else {
            alert("Duplicate action number.\nAction execution sequence #" + (number + 1) + ": action #" + this_sequence + " is already defined.");
            return false;
        }
    }
    return true;
}