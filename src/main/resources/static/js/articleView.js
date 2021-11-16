const viewer = toastui.Editor.factory({
    el: document.querySelector('#viewer'),
    viewer: true,
    height: '500px',
    initialValue: ''
});

const contents = document.getElementById("contents");

viewer.setMarkdown(contents.value);

