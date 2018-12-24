import { Component, OnInit } from '@angular/core';

import { BsModalService } from "ngx-bootstrap";
import { BsModalRef } from "ngx-bootstrap";
import { ModalContentComponent } from "../modal-content/modal-content.component";

import { saveAs } from 'file-saver';

import { Type } from "../type";
import { Document } from "../document";

import { TypeService } from "../services/type.service";
import { ParameterService } from "../services/parameter.service";
import { FileService } from "../services/file.service";
import { MessageService } from "../services/message.service";
import { ModalRfqComponent } from "../model-rfq/modal-rfq.component";

@Component({
  selector: 'app-rfq',
  templateUrl: './rfq.component.html',
  styleUrls: [ './rfq.component.scss' ]
})
export class RfqComponent implements OnInit{
  types: Type[];
  docs: Document[];
  // defaultThreshold: number;
  defaultSimilarityAlgo: number;

  modalRef: BsModalRef;
  constructor(
    private modalService: BsModalService,
    private typeService: TypeService,
    private paramService: ParameterService,
    private fileService: FileService,
    private messageService: MessageService
  ) {}

  ngOnInit() {
    // this.getTypes();
    // this.getParams();
    this.getDocs();
    this.fileService.getProcessingFiles(1)
      .subscribe(data=> {
        this.fileService.close_alert();
        if (data.length != 0) {
          this.fileService.newAlerts(data);
        }
      });
    this.paramService.getParameters()
      .subscribe(params=>{
        this.defaultSimilarityAlgo = params[1].value;
      })
  }

  // getTypes(): void {
  //   this.typeService.getTypes()
  //     .subscribe(types => this.types = types)
  // }

  // getParams(): void {
  //   this.paramService.getParameters()
  //     .subscribe(params => this.defaultThreshold = params[1].value)
  // }

  getDocs(): void {
    this.fileService.getAll(1)
      .subscribe(docs => this.docs = docs)
  }

  openModelWithComponent() {
    const initialState = {
      title: 'Upload RFQ file',
      filetype: 'rfq'
      // thresholdName: 'Matchup threshold',
      // defaultThreshold: this.defaultThreshold,
      // types: this.types
    };
    this.modalRef = this.modalService.show(ModalContentComponent, {initialState});
  }

  openModelWithRfqComponent(doc_id: number) {
    const initialState = {
      title: 'Process preparation',
      doc_id: doc_id,
      similarityAlgorithmArray: [
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
        {"id":11, "value":"Sørensen–Dice系数"}
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
      selectedRfqVar: 0,
      selectedModel: 0,
      selectedSimilarityAlgo: this.defaultSimilarityAlgo
      // defaultSimilarityAlgo: this.defaultSimilarityAlgo
    };
    this.modalRef = this.modalService.show(ModalRfqComponent, {initialState});
  }

  // processDoc(doc_id: number) {
  //   this.fileService.processDoc(doc_id)
  //     .subscribe(message => {
  //       this.docs = message.data as Document[];
  //       this.messageService.new_alert(message.status_code, message.message);
  //     })
  // }

  downloadXlsx(doc_id: number) {
    this.fileService.downloadXlsx(doc_id)
      .subscribe(data => {
        console.log("???");
        saveAs(data);
      });
  }

  downloadDocx(doc_id: number) {
    this.fileService.downloadDocx(doc_id)
      .subscribe(data => {
        console.log("???");
        saveAs(data);
      });
  }
}
