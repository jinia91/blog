const input = document.querySelector('input[name="tags"]');

const tagify = new Tagify(input, {
    whitelist:whitelist,
    maxTags: 10,
    dropdown: {
        maxItems: 20,
        classname: "tags-look",
        enabled: 0,
        closeOnSelect: true
    }
});


