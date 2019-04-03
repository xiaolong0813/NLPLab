import {Component, ElementRef, OnInit, Renderer2, ViewChild} from '@angular/core';

import { TranslationService} from "../services/translation.service";
import { Xmls} from "../xmls";
import {BsModalRef, BsModalService} from "ngx-bootstrap";
import {MessageService} from "../services/message.service";
import {ModalContentComponent} from "../modal-content/modal-content.component";
import {FileService} from "../services/file.service";
import {log} from "util";
import {TranslationDetailComponent} from "../translation-detail/translation-detail.component";

import {interval, Observable, Subject, Subscription} from "rxjs";

import { saveAs } from 'file-saver';
import {forEach} from "@angular/router/src/utils/collection";
import {style} from "@angular/animations";

import * as $ from "jquery"
import {el} from "@angular/platform-browser/testing/src/browser_util";

@Component({
  selector: 'app-translation',
  templateUrl: './translation.component.html',
  styleUrls: ['./translation.component.scss'],
})
export class TranslationComponent implements OnInit {
  public xmls: Xmls[];

  // public selectedXmlEle: object=null;

  public selectedXmlId: number=0;

  //弹窗
  modalRef: BsModalRef;
  // private transDetails: TranslationDetailComponent
  @ViewChild('transDetail')
  private transDetail : TranslationDetailComponent;

  // @ViewChild('testChid')
  // testChid: ElementRef;

  // public processing;
  public processingStart = false;
  public processEmitor = new Subject();
  public processEmitor$ = this.processEmitor.asObservable();

  //emit value in sequence every 1 second
  public source = interval(1000);
  //output: 0,1,2,3,4,5....
  public subscribe: Subscription;

  constructor(
    // element selector
    private el: ElementRef,
    private render2: Renderer2,
    private modalService: BsModalService,
    private transService: TranslationService,
    private fileService : FileService,
    private messageService : MessageService,
  ) { }

  ngOnInit() {
    this.getXMLs();

    // update by xxl
    this.newMessageRefresh();
    this.overallIntervalRefresh();

    // update by sf
    // this.getXMLs();
    // this.subscribe = this.source.subscribe(val => {
    // }
  }

  // update by sf, trigger as leaving this component
  ngOnDestroy() {
    console.log("destroy");
    // this.subscribe.unsubscribe();
  }

  overallIntervalRefresh(): void {
    this.processEmitor$.subscribe(
      res => {
        console.log("get response from emitor");
        if (res && !this.processingStart) {
          console.log("start to process!");
          this.processingStart = true;
          this.subscribe = this.source.subscribe(val => {
            let checkFileProcessing = false;
            console.log("get all xml file status");
              this.getXMLs();
              for (let xml of this.xmls) {
                if (xml.status == 3 || xml.status == 5) {
                  console.log("still processing...");
                  checkFileProcessing = true;
                  break;
                }
              }
              if (!checkFileProcessing) {
                console.log("processing finished!");
                this.processingStart = false;
                this.subscribe.unsubscribe();
              }
            }
          )
        }
        else if (this.processingStart) {
          console.log("the processing has started!")
        }
      }
    )
  }

  eachRowIntervalRefresh(xml_id) {
    let temp = interval(3000).subscribe(val =>
    {
      this.getOneXml(xml_id).subscribe(xml=>{
      if (!xml) {
        console.log("lost!");
        // this.subscribe.unsubscribe();
        temp.unsubscribe();

      }
      else if (xml.status == 4) {
        console.log("stop interval!");
        this.getXMLs();
        // this.subscribe.unsubscribe();
        temp.unsubscribe();
      }
    })
    }
    );
  }

  newMessageRefresh(): void {
    this.messageService.status$.subscribe(
      // res = alert_new
      res => {
        if (res) {
          this.getXMLs();
        }
      }
    )
  }

  getXMLs(): void {
    this.fileService.getAllXMLs()
      .subscribe(xmls => this.xmls = xmls);
  }

  getOneXml(xml_id: number) {
    return this.fileService.getOneXMl(xml_id)
  }

  openModelWithComponent() {
    const initialState = {
      title: 'Upload xml file',
      filetype: 'xml'
    };
    this.modalRef = this.modalService.show(ModalContentComponent, {initialState});
  }

  processXml(xml_id: number) {
    this.transService.processXml(xml_id)
      .subscribe(mes => {
        if(mes.status_code == 200) {
          this.messageService.new_alert(mes.status_code, mes.message);

          this.processEmitor.next(true);

          // 单个interval方法
          // this.eachRowIntervalRefresh(xml_id)
        }
      })
  }

  listDetails(xml_id: number) {
    // if (this.selectedXmlId) {
    //   this.el.nativeElement.querySelector('#checkBtn_'+this.selectedXmlId).style.color='white';
    // }
    this.selectedXmlId = xml_id;
    // this.el.nativeElement.querySelector('#checkBtn_'+xml_id).style.color='red';
    this.transDetail.xml_id = xml_id;
    this.transDetail.getTranslation();
    // console.log(xml_id)
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
          if (mes.status_code == 200) {
            // this.subscribe.unsubscribe();
            this.messageService.new_alert(mes.status_code, mes.message);
            // this.getXMLs();
            this.transDetail.display='none';
            this.selectedXmlId = 0;
          }
        })
    }
  }


  GenerateXML(xmlId: number) {
    this.transService.generateXML(xmlId)
      .subscribe(mes => {
        if (mes.status_code == 200) {
          this.messageService.new_alert(mes.status_code, mes.message);
          this.processEmitor.next(true);
          // this.getXMLs();
        }
      })
  }

  // selectRow(event, row) {
  //   this.selectedXml = row;
  //   event.target.style = "background: green"
  //   console.log(event.target);
  // }

}
