var adjustment;

$("ol.exercises").sortable({
    group: 'simple_with_animation',
    pullPlaceholder: false,
    // animation on drop
    onDrop: function  ($item, container, _super) {
        var $clonedItem = $('<li/>').css({height: 0});
        $item.before($clonedItem);
        $clonedItem.animate({'height': $item.height()});

        $item.animate($clonedItem.position(), function  () {
            $clonedItem.detach();
            _super($item, container);
        });
    },

    // set $item relative to cursor position
    onDragStart: function ($item, container, _super) {
        var offset = $item.offset(),
            pointer = container.rootGroup.pointer;

        adjustment = {
            left: pointer.left - offset.left,
            top: pointer.top - offset.top
        };

        _super($item, container);
    },
    onDrag: function ($item, position) {
        $item.css({
            left: position.left - adjustment.left,
            top: position.top - adjustment.top
        });
    }
});
$(function  () {
    $("ol.exercises").sortable();
});

function sumbitActiveExercises() {
    let obj = {};
    let arr = [];

    for(let i = 0;i<document.getElementById("activeExercises").children.length;i++){
        arr.push(document.getElementById("activeExercises").children[i].dataset.exid);
    }

    obj.exIds = arr;
    sendRequest("post","setActiveExercises",obj,function (status,res) {
        if(status == 200){
            alert("Success!");
            loadHomepage();
        }
    })
}