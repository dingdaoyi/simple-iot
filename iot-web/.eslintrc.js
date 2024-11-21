module.exports = {
  env: {
    browser: true,
    es2021: true,
    node: true,
    'vue/setup-compiler-macros': true
  },
  extends: ['plugin:vue/vue3-essential', 'standard'],
  parserOptions: {
    ecmaVersion: 12,
    sourceType: 'module'
  },
  plugins: ['vue'],
  rules: {
    'vue/multi-word-component-names': 0, // 关闭组件没有名称定义
    'no-unused-vars': 'off',
    'vue/no-mutating-props': 0,
    'vue/no-setup-props-destructure': 0,
    'vue/no-deprecated-v-on-native-modifier': 0,
    'no-unused-expressions': 0,
    'no-irregular-whitespace': 0,
    'no-useless-escape': 0,
    camelcase: 'off',
    'vue/prefer-import-from-vue': 0,
    'prefer-const': 0,
    'no-undef': 0,

    'no-mixed-spaces-and-tabs': 2, // 不能空格与tab混用
    'vue/max-attributes-per-line': [
      2,
      {
        singleline: { max: 7 },
        multiline: { max: 7 }
      }
    ], // vue template模板元素第一行最多5个属性

    // html 闭括号之前无空格
    'vue/html-closing-bracket-spacing': 2,

    // html 需要有结束标签，除了自闭合标签
    'vue/html-end-tags': 2,

    // 缩进
    'vue/html-indent': 2,

    // 属性-引用-默认使用双引号
    'vue/html-quotes': 2,

    // 标签里面不允许多余空格
    'vue/no-multi-spaces': 2,
    'no-eval': 0,

    'vue/mustache-interpolation-spacing': 2, // {{ val }}
    'vue/no-spaces-around-equal-signs-in-attribute': 2, // 属性间不允许空格
    'vue/attributes-order': [
      'error',
      {
        order: [
          'DEFINITION',
          'LIST_RENDERING',
          'CONDITIONALS',
          'RENDER_MODIFIERS',
          'GLOBAL',
          ['UNIQUE', 'SLOT'],
          'TWO_WAY_BINDING',
          'OTHER_DIRECTIVES',
          'OTHER_ATTR',
          'EVENTS',
          'CONTENT'
        ],
        alphabetical: false
      }
    ] /// class = "66"  属性间不允许空格
  }
}
