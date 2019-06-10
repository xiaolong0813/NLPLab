// This file can be replaced during build by using the `fileReplacements` array.
// `ng build ---prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  apiBase: "http://localhost:7890",
  simAlgo: [
    // {"id":0, "value":"编辑距离算法"},
    // {"id":1, "value":"Gregor编辑距离法"},
    // {"id":2, "value":"优化编辑距离法"},
    // {"id":3, "value":"词性和词序结合法"},
    // {"id":4, "value":"余弦相似度"},
    // {"id":5, "value":"欧几里得距离"},
    // {"id":6, "value":"Jaccard相似性系数"},
    // {"id":7, "value":"Jaro距离"},
    // {"id":8, "value":"Jaro–Winkler距离"},
    // {"id":9, "value":"曼哈顿距离"},
    // {"id":10, "value":"SimHash + 汉明距离"},
    // {"id":11, "value":"Sørensen–Dice系数"},
    {"id":12, "value":"Levenshtein距离"}
  ],
  ZPFModelArray: [
    {"id":0, "value":"F1x1"},
    {"id":1, "value":"H1x1"},
    {"id":2, "value":"F1S"},
    {"id":3, "value":"H1S"},
    {"id":4, "value":"HL1x1"}
  ],
  RfqVarArray: [
    {"id":0, "value":"A"},
    {"id":1, "value":"B"},
    {"id":2, "value":"C"},
    {"id":3, "value":"D"}
  ],
  compareLevel: [
    {"id":0, "value":"Paragraph"},
    {"id":1, "value":"Sentence"}
  ]
};

/*
 * In development mode, to ignore zone related error stack frames such as
 * `zone.run`, `zoneDelegate.invokeTask` for easier debugging, you can
 * import the following file, but please comment it out in production mode
 * because it will have performance impact when throw error
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
