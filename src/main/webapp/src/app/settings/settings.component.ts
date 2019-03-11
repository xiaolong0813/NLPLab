import { Component, OnInit } from '@angular/core';

import { Type } from "../type";

import { TypeService } from "../services/type.service";
import { ParameterService } from "../services/parameter.service";
import { MessageService } from "../services/message.service";
import { Parameter } from "../parameter";

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: [ './settings.component.scss' ]
})

export class SettingsComponent implements OnInit{
  // groupThreshold: Parameter;
  // matchupThreshold: Parameter;
  checkNewSentences: Parameter;
  similarityAlgorithm: Parameter;
  similarityAlgorithmArray: object;
  types: Type[];

  constructor(private typeService: TypeService, private paramService:ParameterService, private messageService: MessageService){}

  ngOnInit() {
    // this.groupThreshold = 0.1;
    // this.matchupThreshold = 0.1;
    this.similarityAlgorithmArray = [
      {"id":0, "value":"编辑距离算法"},
      {"id":1, "value":"Gregor编辑距离法"},
      {"id":2, "value":"优化编辑距离法"},
      {"id":3, "value":"词性和词序结合法"},
      {"id":4, "value":"余弦相似度"},
      {"id":5, "value":"欧几里得距离"},
      {"id":6, "value":"Jaccard相似性系数"},
      {"id":7, "value":"Jaro距离"},
      {"id":8, "value":"Jaro–Winkler距离"},
      {"id":9, "value":"曼哈顿距离"},
      {"id":10, "value":"SimHash + 汉明距离"},
      {"id":11, "value":"Sørensen–Dice系数"},
      {"id":12, "value":"Levenshtein距离"}
      ];
    // this.getTypes();
    this.getParams();
  }

  // getTypes(): void {
  //   this.typeService.getTypes()
  //     .subscribe(types => this.types = types)
  // }
  //
  // addType(typeName: string): void {
  //   typeName = typeName.trim();
  //   if (!typeName) { return; }
  //
  //   let new_type = {
  //     "name": typeName
  //   };
  //   this.typeService.addType(new_type as Type)
  //     .subscribe(message => {
  //       this.types = message.data as Type[];
  //       this.messageService.new_alert(message.status_code, message.message);
  //     }
  //   )
  // }
  //
  // deleteType(typeId: number): void {
  //   if (confirm("Are you sure? This operation will remove all the relevant deviation records.")) {
  //     this.typeService.deleteType(typeId)
  //       .subscribe(message=>{
  //         this.types = message.data as Type[];
  //         this.messageService.new_alert(message.status_code, message.message);
  //       })
  //   }
  // }

  getParams():void {
    this.paramService.getParameters()
      .subscribe(params=>{
        // this.groupThreshold = params[0];
        // this.matchupThreshold = params[1];
        this.checkNewSentences = params[0];
        this.similarityAlgorithm = params[1];
      })
  }

  // changeGroupThreshold(value: number) {
  //   this.groupThreshold.value = value;
  //   this.paramService.updateParameter(this.groupThreshold)
  //     .subscribe(message=>{
  //       this.groupThreshold = message.data[0] as Parameter;
  //       this.messageService.new_alert(message.status_code, message.message);
  //     })
  // }
  //
  // changeMatchupThreshold(value: number) {
  //   this.matchupThreshold.value = value;
  //   this.paramService.updateParameter(this.matchupThreshold)
  //     .subscribe(message=>{
  //       this.matchupThreshold = message.data[1] as Parameter;
  //       this.messageService.new_alert(message.status_code, message.message);
  //     })
  // }

  changeCheckNewSentences() {
    this.checkNewSentences.value = 1 - this.checkNewSentences.value;
    this.paramService.updateParameter(this.checkNewSentences)
      .subscribe(message=> {
        this.checkNewSentences = message.data[0] as Parameter;
        this.messageService.new_alert(message.status_code, message.message);
      })
  }

  changeSimilarityAlgorithm(value: number) {
    this.similarityAlgorithm.value = value;
    this.paramService.updateParameter(this.similarityAlgorithm)
      .subscribe(message=>{
        this.similarityAlgorithm = message.data[1] as Parameter;
        this.messageService.new_alert(message.status_code, message.message);
      })
  }
}
