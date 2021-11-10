const arrow = document.getElementById("arrow");

arrow.addEventListener('click', () => {
    window.scrollTo(0, 0);
})

window.addEventListener("scroll", () => {

    if (scrollY != 0) {
        arrow.style.visibility = 'visible';
    } else {
        arrow.style.visibility = 'hidden';
    }
}, {passive: true});
