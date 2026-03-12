import js from '@eslint/js'
import reactPlugin from 'eslint-plugin-react'
import reactHooks from 'eslint-plugin-react-hooks'
import reactRefresh from 'eslint-plugin-react-refresh'
import prettierConfig from 'eslint-config-prettier'
import prettierPlugin from 'eslint-plugin-prettier'

export default [
  // Temel JS kurallari
  js.configs.recommended,

  // React + hooks
  {
    files: ['src/**/*.{js,jsx,ts,tsx}'],
    plugins: {
      react: reactPlugin,
      'react-hooks': reactHooks,
      'react-refresh': reactRefresh,
      prettier: prettierPlugin,
    },
    languageOptions: {
      ecmaVersion: 2022,
      sourceType: 'module',
      parserOptions: {
        ecmaFeatures: { jsx: true },
      },
      globals: {
        window: 'readonly',
        document: 'readonly',
        console: 'readonly',
        localStorage: 'readonly',
        fetch: 'readonly',
        confirm: 'readonly',
        alert: 'readonly',
        Promise: 'readonly',
        setTimeout: 'readonly',
        clearTimeout: 'readonly',
      },
    },
    settings: {
      react: { version: 'detect' },
    },
    rules: {
      // React kurallari
      ...reactPlugin.configs.recommended.rules,
      ...reactHooks.configs.recommended.rules,
      'react/react-in-jsx-scope': 'off',       // React 17+ icin gereksiz
      'react/prop-types': 'off',               // TypeScript kullanmiyoruz
      'react-refresh/only-export-components': ['warn', { allowConstantExport: true }],

      // Genel kalite kurallari
      'no-unused-vars': ['warn', { argsIgnorePattern: '^_', varsIgnorePattern: '^_' }],
      'no-console': ['warn', { allow: ['warn', 'error'] }],
      'prefer-const': 'error',
      'no-var': 'error',
      'eqeqeq': ['error', 'always'],

      // Prettier entegrasyonu (format hatalarini ESLint hatasi olarak goster)
      'prettier/prettier': 'error',
    },
  },

  // Prettier kurallari (ESLint ile catisan format kurallarini kapat)
  prettierConfig,

  // Test ve config dosyalari icin daha esnek kurallar
  {
    files: ['**/*.config.js', '**/*.config.ts', 'vite.config.*'],
    rules: {
      'no-console': 'off',
    },
  },

  // Ignore listesi
  {
    ignores: ['dist/**', 'node_modules/**', 'build/**'],
  },
]
