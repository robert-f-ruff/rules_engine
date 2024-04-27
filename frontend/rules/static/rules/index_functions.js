function callEngineReload() {
    parseEngineResponse(window.location.href + "engine/reload/")
}

async function parseEngineResponse(location) {
    let response = await fetch(location);
    let engine_response = await response.json();
    let engine_status_text = document.getElementById("engine_status")
    let reload_status_text = document.getElementById("reload_status");
    engine_status_text.innerText = engine_response.engine_status_text_status;
    reload_status_text.innerText = engine_response.engine_status_text_reload;
    if (engine_response.engine_alert == "False") {
        let status_area = document.getElementById("engine_status_area");
        status_area.setAttribute("class", "alert alert-success");
        let reload_button = document.getElementById("reload_button");
        hideControl(reload_button);
    }
}