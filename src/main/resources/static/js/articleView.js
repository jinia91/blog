const viewer = toastui.Editor.factory({
    el: document.querySelector('#viewer'),
    viewer: true,
    height: '500px',
    initialValue: ''
});

const contents = document.getElementById("contents");

viewer.setMarkdown(contents.value);



function deleteArticle() {


    if (confirm("글을 정말 삭제하시겠습니까?") == true) {
        document.getElementById("deleteArticle").submit();
    } else {
        return false;
    }


}