import { Component, OnInit } from '@angular/core';

import { BsModalService } from "ngx-bootstrap";
import { BsModalRef } from "ngx-bootstrap";
import { ModalContentComponent } from "../modal-content/modal-content.component";

import { saveAs } from 'file-saver/FileSaver';

import { Type } from "../type";
import { Document } from "../document";

import { TypeService } from "../services/type.service";
import { ParameterService } from "../services/parameter.service";
import { FileService } from "../services/file.service";
import {MessageService} from "../services/message.service";

@Component({
  selector: 'app-rfq',
  templateUrl: './rfq.component.html',
  styleUrls: [ './rfq.component.scss' ]
})
export class RfqComponent implements OnInit{
  types: Type[];
  docs: Document[];
  defaultThreshold: number;

  modalRef: BsModalRef;
  constructor(
    private modalService: BsModalService,
    private typeService: TypeService,
    private paramService: ParameterService,
    private fileService: FileService,
    private messageService: MessageService
  ) {}

  ngOnInit() {
    this.getTypes();
    this.getParams();
    this.getDocs();
    this.fileService.getProcessingFiles(1)
      .subscribe(data=> {
        this.fileService.close_alert();
        if (data.length != 0) {
          this.fileService.newAlerts(data);
        }
      })
  }

  getTypes(): void {
    this.typeService.getTypes()
      .subscribe(types => this.types = types)
  }

  getParams(): void {
    this.paramService.getParameters()
      .subscribe(params => this.defaultThreshold = params[1].value)
  }

  getDocs(): void {
    this.fileService.getAll(1)
      .subscribe(docs => this.docs = docs)
  }

  openModelWithComponent() {
    const initialState = {
      title: 'Upload RFQ file',
      thresholdName: 'Matchup threshold',
      defaultThreshold: this.defaultThreshold,
      types: this.types
    };
    this.modalRef = this.modalService.show(ModalContentComponent, {initialState});
  }

  processDoc(doc_id: number) {
    this.fileService.processDoc(doc_id)
      .subscribe(message => {
        this.docs = message.data as Document[];
        this.messageService.new_alert(message.status_code, message.message);
      })
  }

  downloadDoc(doc_id: number) {
    this.fileService.downloadDoc(doc_id)
      .subscribe(data => {
        saveAs(data);
      });
  }
}
