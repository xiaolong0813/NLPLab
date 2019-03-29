import {Component, OnInit, Renderer2, ViewChild} from '@angular/core';

import { TranslationService} from "../services/translation.service";
import { Xmls} from "../xmls";
import {BsModalRef, BsModalService} from "ngx-bootstrap";
import {MessageService} from "../services/message.service";
import {ModalContentComponent} from "../modal-content/modal-content.component";
import {FileService} from "../services/file.service";
import {log} from "util";
import {TranslationDetailComponent} from "../translation-detail/translation-detail.component";

import { saveAs } from 'file-saver';

@Component({
  selector: 'app-translation',
  templateUrl: './translation.component.html',
  styleUrls: ['./translation.component.scss']
})
export class TranslationComponent implements OnInit {
  xmls: Xmls[];
  //弹窗
  modalRef: BsModalRef;
  // private transDetails: TranslationDetailComponent
  @ViewChild('transDetail')
  transDetail : TranslationDetailComponent;


  constructor(
    // element selector
    private render2: Renderer2,
    private modalService: BsModalService,
    private transService: TranslationService,
    private fileService : FileService,
    private messageService : MessageService,
  ) { }

  ngOnInit() {
    this.getXMLs();
    // this.fileService
  }

  getXMLs(): void {
    this.fileService.getAllXMLs()
      .subscribe(xmls => this.xmls = xmls);
  }

  openModelWithComponent() {
    const initialState = {
      title: 'Upload xml file',
      filetype: 'xml'
    };
    this.modalRef = this.modalService.show(ModalContentComponent, {initialState});
  }


  openModelWithTransComponent(id: number) {

  }


  processXml(xml_id: number) {
    this.transService.processXml(xml_id)
      .subscribe(mes => {
        if(mes.status_code == 200) {
          this.getXMLs();
          // location.reload();
          // console.log(mes.data)
        }
      })
  }

  listDetails(xml_id: number) {
    this.transDetail.xml_id = xml_id;
    this.transDetail.getTranslation();
    console.log(xml_id)
  }


  downloadXML(xml_id: number) {
    if (confirm("Do you want to download xml file based on current translations? ")) {
      this.fileService.downloadXML(xml_id)
        .subscribe(data => {
          console.log('download xml file');
          console.log(data);
          saveAs(data);
        })
    }
  }

  removeAllXmls() {
    if (confirm("Are you sure to remove all the Xml files (including all translation)?")) {
      this.fileService.removeAllXmls()
        .subscribe(mes => {
          this.messageService.new_alert(mes.status_code, mes.message);
          this.getXMLs();
          this.transDetail.display='none';
        })
    }
  }


  GenerateXML(xmlId: number) {
    this.transService.generateXML(xmlId)
      .subscribe(mes => {
        if (mes.status_code == 200) {
          this.getXMLs();
        }
      })
  }
}
