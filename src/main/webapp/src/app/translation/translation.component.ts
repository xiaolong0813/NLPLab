import {Component, ElementRef, OnDestroy, OnInit, Renderer2, ViewChild} from '@angular/core';

import { TranslationService} from "../services/translation.service";
import { Xmls} from "../xmls";
import { Document } from "../document";
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
import {EmitorService} from "../services/emitor.service";

@Component({
  selector: 'app-translation',
  templateUrl: './translation.component.html',
  styleUrls: ['./translation.component.scss'],
})
export class TranslationComponent implements OnInit, OnDestroy {
  public xmls: Xmls[];

  public selectedXmlId: number;

  //弹窗
  modalRef: BsModalRef;

  @ViewChild('transDetail')
  private transDetail : TranslationDetailComponent;

  // public processing;
  // public processingStart: boolean;
  // public processEmitor = new Subject();
  // public processEmitor$ = this.processEmitor.asObservable();

  public timeC = interval(1000);
  public timeC$ : Subscription;

  constructor(
    // element selector
    private el: ElementRef,
    private render2: Renderer2,
    private modalService: BsModalService,
    private transService: TranslationService,
    private fileService : FileService,
    private messageService : MessageService,
    private emitorService: EmitorService,
  ) { }

  ngOnInit() {
    this.getXMLs();

    // 每次进入组件创建实例会重建

    this.checkProcessing();


    // 订阅事件发生器.主要接受来自弹窗的通知刷新页面
    this.newEventRefresh();

    // console.log(this.transDetail)

    // console.log(this.emitorService.transEmitor)

    // 每次调用都会创建一个Observor。当Subject调用next方法时，会给每个observor调用next方法
    // 相当于每次进入初始化组件都会建立一个观察者对象
    // this.overallIntervalRefresh();

    // 本地储存所选择的id，再次进入可自动获取并显示。localstorage大小限于5M
    // console.log(localStorage.getItem("xmlId"))
    // var localId = localStorage.getItem("xmlId");
    // if (localId) {
    //   this.selectedXmlId = Number(localId);
    //   this.transDetail.xml_id = Number(localId);
    //   this.transDetail.getTranslation();
    // }

  }

  // update by sf, trigger as leaving this component
  ngOnDestroy() {
    console.log("destroy trans");
    // this.timeC$.unsubscribe()
    if (this.timeC$) {
      console.log("unsubscribe trans");
      this.timeC$.unsubscribe();
    }
    // 取消这个组件订阅者
    this.emitorService.clearObservors(this.emitorService.transEmitor);
  }

  checkProcessing(): void {
    if (this.emitorService.transTimerStart) {
      console.log("xmls processing is going on");
      this.createTimer();
    }
    else {
      console.log("no processing xmls")
    }
  }

  newEventRefresh(): void {
    // 创建观察者
    this.emitorService.getEvent(this.emitorService.transEmitor).
    subscribe(value => {
      if (value) {
        console.log("get alert from emitor service");
        this.getXMLs();
      }
    });
  }

  overallIntervalRefresh(): void {
      if (!this.emitorService.transTimerStart) {
        console.log("start to process xml!");
        this.emitorService.transTimerStart = true;
        this.createTimer();
      }
      else {
        console.log("the processing has started!")
      }
  }

  createTimer(): void {
    this.timeC$ =
      this.timeC.subscribe(value => {
          console.log("get xml status");
          this.getXMLs();
          this.transService.checkProcessingXML().subscribe(
            value1 => {
              if (!value1){
                this.timeC$.unsubscribe();
                this.emitorService.transTimerStart = false;
                console.log("finish processing xmls")
              }
              else {
                console.log("still processing xmls")
              }
            }
          )
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

          this.getXMLs();

          // 开始持续刷新
          this.overallIntervalRefresh()

          // this.emitorService.transProcessEmitor.next(true);
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

    localStorage.setItem("xmlId", xml_id.toString())
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

            this.getXMLs();

            // this.emitorService.emitEvent(this.emitorService.transEmitor);
            // this.getXMLs();
            this.transDetail.display='none';
            this.selectedXmlId = 0;

            // console.log(this.emitorService.transProcessEmitor)

          }
        })
    }
  }

  GenerateXML(xmlId: number) {
    this.transService.generateXML(xmlId)
      .subscribe(mes => {
        if (mes.status_code == 200) {
          this.messageService.new_alert(mes.status_code, mes.message);

          this.getXMLs();

          // 开始持续刷新
          this.overallIntervalRefresh()
        }
      })
  }


  // overallIntervalRefreshEmitor() : void{
  //   this.emitorService.transProcessEmitor$.subscribe(
  //     value => {
  //       console.log(this.emitorService.transProcessEmitor);
  //
  //       console.log("get response from emitor");
  //       if (!this.emitorService.transTimerStart) {
  //         console.log("start to process xml!");
  //         this.emitorService.transTimerStart = true;
  //         this.createTimer();
  //       }
  //       else {
  //         console.log("the processing has started!")
  //       }
  //     }
  //   )
  // }


  // overallIntervalRefresh(): void {
  // this.processEmitor$.subscribe(
  //   res => {
  //     console.log("get response from emitor");
  //     if (res && !this.processingStart) {
  //       console.log("start to process xml!");
  //       this.processingStart = true;
  //       this.subscribe = this.source.subscribe(val => {
  //         let checkFileProcessing = false;
  //         console.log("get all xml file status");
  //           this.getXMLs();
  //           for (let xml of this.xmls) {
  //             if (xml.status == 3 || xml.status == 5) {
  //               console.log("still processing...");
  //               checkFileProcessing = true;
  //               break;
  //             }
  //           }
  //           if (!checkFileProcessing) {
  //             setTimeout(() => {
  //               console.log("processing finished!");
  //               this.getXMLs();
  //               this.processingStart = false;
  //               this.subscribe.unsubscribe();
  //             },2000)
  //
  //           }
  //         }
  //       )
  //     }
  //     else if (this.processingStart) {
  //       console.log("the processing has started!")
  //     }
  //   }
  // )
  // }

  // eachRowIntervalRefresh(xml_id) {
  //   let temp = interval(3000).subscribe(val =>
  //     {
  //       this.getOneXml(xml_id).subscribe(xml=>{
  //         if (!xml) {
  //           console.log("lost!");
  //           // this.subscribe.unsubscribe();
  //           temp.unsubscribe();
  //
  //         }
  //         else if (xml.status == 4) {
  //           console.log("stop interval!");
  //           this.getXMLs();
  //           // this.subscribe.unsubscribe();
  //           temp.unsubscribe();
  //         }
  //       })
  //     }
  //   );
  // }

}
