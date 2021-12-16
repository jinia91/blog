# tui-font-size-picker

A plugin to help users edit font sizes in the [TUI Editor](https://github.com/nhn/tui.editor)

![Plugin](https://github.com/C-likethis123/tui-font-size-picker/blob/master/Font%20Size%20Plugin%20Demo.gif?raw=true)

## Features

Supports editing font sizes in both the Markdown and WYSIWYG modes.

## Installation

Install this package with the package manager of your choice.

### npm

`npm install tui-font-size-picker`

### yarn

`yarn add tui-font-size-picker`

## Usage

### ES Modules

```javascript
import fontSizePicker from 'tui-font-size-picker';
```

### CommonJS

```javascript
const fontSizePicker = require('tui-font-size-picker')
```

### Usage in TUI Editor

To use this in the TUI Editor, it is required to install the [TUI Editor](https://github.com/nhn/tui.editor) or its relevant wrappers (eg React, Vue wrappers) in your project. Then create a new Editor instance and the plugin:

```javascript
const editor = new Editor({
  el: document.querySelector('#editor'),
  previewStyle: 'vertical',
  height: '500px',
  initialEditType: 'markdown',
  initialValue: 'Hello!',
  plugins: [fontSizePicker] // include the font size picker as a plugin
});
```

## Developing

Clone this repository: `git clone https://github.com/C-likethis123/tui-font-size-picker`

Install dependencies: `yarn install` or `npm install`

To run: `yarn serve` or `npm run serve`

Navigate to `localhost:8080/demo/editor.html` to load the demo.
