$(document).ready(function() {
    addPutAndDeleteMethods();
    addUpdateButtonCallbacks();
    updateRowHighlights();
});

function addPutAndDeleteMethods() {
    jQuery.each( [ "put", "delete" ], function( i, method ) {
        jQuery[ method ] = function( url, data, callback, type ) {
            if ( jQuery.isFunction( data ) ) {
                type = type || callback;
                callback = data;
                data = undefined;
            }

            return jQuery.ajax({
                url: url,
                type: method,
                dataType: type,
                data: data,
                success: callback
            });
        };
    });
}

function addUpdateButtonCallbacks() {
    $(".bubbles button").click(function() {
        var characterKey = $(this).attr('characterKey');
        var statName = $(this).attr('statName');
        var statValue = $(this).attr('statValue');
        updateStat(characterKey, statName, statValue);
    });
}

function updateStat(characterKey, statName, statValue) {
    var url = "/character/" + characterKey + "/stats/" + statName;
    console.log("posting to " + url);
    var data = { value: statValue };
    $.put(
        url,
        data,
        function(data, status){
            updatePage(characterKey, statName, statValue, data, status);
        },
        "json"
    );
    console.log("posted to " + url);
}

function updatePage(characterKey, statName, statValue, data, status) {
    updateCurrentValue(characterKey, statName, statValue, data, status);
    updateButtonClasses(characterKey, statName, statValue, data, status);
    updateRowHighlights();
}

function updateCurrentValue(characterKey, statName, statValue, data, status) {
    $('.' + statName + '-current').text(statValue)
}
function updateButtonClasses(characterKey, statName, statValue, data, status) {
    var iStatValue = parseInt(statValue);
    var buttons = $("." + statName + "-section button");
    buttons.removeClass();
    buttons.each(function(index){
        var iButtonStatValue = parseInt($(this).attr('statValue'));
        var newClass = (iStatValue < iButtonStatValue) ? "bubble-used" :
            (iStatValue == iButtonStatValue) ? "bubble-selected" :
            "bubble";
        $(this).addClass(newClass);
    });
}

function updateRowHighlights() {
    $("tr").removeClass("selected-row");
    $(".bubble-selected").closest("tr").addClass("selected-row");

}
