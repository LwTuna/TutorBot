

$(document).ready(function () {
    const editor = ace.edit("editor");
    editor.setTheme("ace/theme/monokai");
    editor.getSession().setMode("ace/mode/java");
    editor.setReadOnly(true);

});

