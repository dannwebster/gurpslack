$(document).ready(function() {
    addPutAndDeleteMethods();
    addStatUpdateButtonCallbacks();
    addAmountUpdateButtonCallbacks();
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

function addStatUpdateButtonCallbacks() {
    addUpdateButtonCallbacks("stats", "", updatePage);
}

function addAmountUpdateButtonCallbacks() {
    addUpdateButtonCallbacks("amounts", "#amt-section ", updateAmount);
}

function addUpdateButtonCallbacks(resourceName, selectorPrefix, callbackFunction) {
    $(selectorPrefix + ".bubbles button").click(function() {
        var characterKey = $(this).attr('characterKey');
        var statName = $(this).attr('statName');
        var statValue = $(this).attr('statValue');
        updateStat(characterKey, statName, statValue, resourceName, callbackFunction);
    });
}

function updateStat(characterKey, statName, statValue, resourceName, callbackFunction) {
    var url = "/character/" + characterKey + "/" + resourceName + "/" + statName;
    console.log("putting to " + url);
    var data = { value: statValue };
    $.put(
        url,
        data,
        function(data, status){
            callbackFunction(characterKey, statName, data, status);
        },
        "json"
    );
    console.log("put to " + url);
}

function updatePage(characterKey, statName, data, status) {
    var newStatValue = data.value;
    var timestamp = data.lastUpdated;

    console.log("updating page from data '" + data + "' with timestamp " + timestamp + " and newStatValue " + newStatValue);

    updateCurrentValue(characterKey, statName, newStatValue, data, status);
    updateButtonClasses(characterKey, statName, newStatValue, data, status);
    updateTimestamp(timestamp);
    updateRowHighlights();
}

function updateAmount(characterKey, statName, data, status) {
    var newStatValue = data.value;
    var timestamp = data.lastUpdated;

    console.log("updating amount from data '" + data + "' with timestamp " + timestamp + " and newStatValue " + newStatValue);

    updateButtonClasses(characterKey, "amt", newStatValue, data, status);
    updateTimestamp(timestamp);
}

function updateCurrentValue(characterKey, statName, statValue, data, status) {
    $('.' + statName + '-current').text(statValue)
}

function updateButtonClasses(characterKey, statName, statValue, data, status) {
    var iStatValue = parseInt(statValue);
    console.log("iStatValue: " + iStatValue)
    var buttons = $("." + statName + "-section button");
    buttons.removeClass();
    buttons.each(function(index){
        var iButtonStatValue = parseInt($(this).attr('statValue'));
        var newClass = (iStatValue < iButtonStatValue) ? "bubble-used" :
            (iStatValue == iButtonStatValue) ? "bubble-selected" :
            "bubble";

        console.log("iButtonStatValue: " + iButtonStatValue)
        console.log("iStatValue: " + iStatValue)
        console.log("newClass: " + newClass)

        $(this).addClass(newClass);
    });
}

function updateRowHighlights() {
    $("tr").removeClass("selected-row");
    $(".bubble-selected").closest("tr").addClass("selected-row");

}

function adjustByAmount(characterKey, statName, multiplier) {
    var adjustment = $("#" + statName + "-amt").val();
    console.log("adjusting " + characterKey + "." + statName + " by " + adjustment);
    var iAdjustment = parseInt(adjustment) * multiplier;
    console.log("adjusting " + characterKey + "." + statName + " by " + iAdjustment);
    adjust(characterKey, statName, iAdjustment);
}

function decByAmount(characterKey, statName) {
    console.log("decrementing " + characterKey + "." + statName);
    adjustByAmount(characterKey, statName, -1);
}

function incByAmount(characterKey, statName) {
    console.log("incrementing " + characterKey + "." + statName);
    adjustByAmount(characterKey, statName, 1);
}

function adjust(characterKey, statName, adjustment) {
    var url = "/character/" + characterKey + "/stats/" + statName + "/adjustment";
    console.log("posting to " + url);
    var data = {
        adjustment: adjustment
    };
    $.post(
        url,
        data,
        function(data, status){
            updatePage(characterKey, statName, data, status);
        },
        "json"
    );
    console.log("posted to " + url);
}

function updateTimestamp(timestamp) {
    $('#timestamp').text(timestamp);
}
