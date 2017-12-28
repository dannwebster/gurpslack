$(document).ready(function() {
    $("#bubbles span").click(function() {
        $(".overlay").toggle();
    });
});

function updateStat(characterKey, statName, value) {
    var url = "/character/" + characterKey + "/stats/" + statName;
    console.log("posting to " + url);
    var data = { value: value };
    $.post(
        url,
        data,
        function(data, status){
            console.log("updatePageCallback: " + status + " => " + data.first + ": " + data.second);
        },
        "json"
    );
    console.log("posted to " + url);
}

function updatePage(data, status) {
    console.log("updatePageCallback: " + status + " => " + data.first + ": " + data.second);
}