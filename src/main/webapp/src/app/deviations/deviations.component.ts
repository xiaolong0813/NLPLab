import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';

import { BsModalService } from "ngx-bootstrap";
import { BsModalRef } from "ngx-bootstrap";
import { ModalContentComponent } from "../modal-content/modal-content.component";

import { Type } from "../type";
import { Parameter } from "../parameter";

import { TypeService } from "../services/type.service";
import { ParameterService } from "../services/parameter.service";
import {DeviationService} from "../services/deviation.service";
import {MessageService} from "../services/message.service";
import {DeviationDetailComponent} from "../deviation-detail/deviation-detail.component";
import {componentRefresh} from "@angular/core/src/render3/instructions";
import {Document} from "../document";
import {FileService} from "../services/file.service";
import {interval, Subscription} from "rxjs";
import {el} from "@angular/platform-browser/testing/src/browser_util";
import {EmitorService} from "../services/emitor.service";

@Component({
  selector: 'app-deviations',
  templateUrl: './deviations.component.html',
  styleUrls: [ './deviations.component.scss' ]
})
export class DeviationsComponent implements OnInit, OnDestroy, AfterViewInit{
  types: Type[];

  // 如果重载会覆盖原来的值
  // public isProcessingDevDocs: boolean;

  public processingDocs: Document[];

  @ViewChild('devDetail')
  private devDetail: DeviationDetailComponent;
  // reloadFlag: number;
  // defaultThreshold: number;

  public timeC = interval(1000);
  public timeC$ : Subscription;

  modalRef: BsModalRef;
  constructor(private modalService: BsModalService,
              private deviationService: DeviationService,
              private messageService: MessageService,
              private fileService: FileService,
              private emitorService: EmitorService) {
  }

  ngOnInit() {

    this.devDetail.getDevs();

    this.checkProcessing();
    // 用ViewChild装饰器获取的元素，在constructor和ngOnInit中是undefined，
    // 在AfterViewInit中可以获取到。
    // this.devDetail.getDevs();

    // this.newMessageRefresh();
    // this.overallIntervalRefresh()

    this.newEventResponse();

    // this.reloadFlag = 1;
    // this.getTypes();
    // this.getParams();
  }

  // 父组件初始化完毕后执行
  ngAfterViewInit(){
    console.log("父组件的视图初始化完毕");
    // this.devDetail.getDevs();
  }

  ngOnDestroy() {
    console.log("destroy dev");
    if (this.timeC$) {
      console.log("unsubscribe dev");
      this.timeC$.unsubscribe();
    }
    // 取消这个组件订阅者
    this.emitorService.clearObservors(this.emitorService.devEmitor);
  }

  checkProcessing(): void {
    if (this.emitorService.devTimerStart) {
      console.log("devs processing is going on");
      this.createTimer();
    }
    else {
      console.log("no processing devs")
    }
  }

  checkIfStart(): boolean {
    return this.emitorService.devTimerStart;
  }

  newEventResponse(): void {
    // 创建观察者
    this.emitorService.getEvent(this.emitorService.devEmitor).
    subscribe(value => {
      if (value == "refresh") {
        console.log("get alert from dev emitor service: refresh");
        this.devDetail.getDevs();
      }
      else if (value == "timer") {
        console.log("get alert from dev emitor service: timer");
        if (!this.emitorService.devTimerStart) {
          console.log("start to process dev!");
          this.emitorService.devTimerStart = true;
          this.createTimer();
        }
        else {
          console.log("the processing has started!")
        }
      }
    });
  }

  createTimer(): void {
    this.timeC$ =
      this.timeC.subscribe(value => {
          console.log("get dev and sents status");
          // fileType set to 20 to check both devs and doc sents processing
          this.fileService.checkProcessingFiles(20).subscribe(
            found => {
              if (!found){
                console.log("finish processing devs or sents");
                this.timeC$.unsubscribe();
                this.emitorService.devTimerStart = false;
                this.devDetail.getDevs();
              }
              else {
                console.log("still processing devs or sents")
              }
            }
          )
        }
      )
  }

  openModelWithComponent_Dev() {
    const initialState = {
      title: 'Upload deviation file',
      filetype: 'deviation'
      // thresholdName: 'Group threshold',
      // defaultThreshold: this.defaultThreshold,
      // types: this.types
    };
    this.modalRef = this.modalService.show(ModalContentComponent, {initialState});
  }

  openModelWithComponent_DevSrc() {
    const initialState = {
      title: 'Upload deviation source file',
      filetype: 'deviation_src'
      // thresholdName: 'Group threshold',
      // defaultThreshold: this.defaultThreshold,
      // types: this.types
    };
    this.modalRef = this.modalService.show(ModalContentComponent, {initialState});
  }

  removeAllDev() {
    if (confirm("Are you sure to remove all the deviation records?")) {
      this.deviationService.removeAllDevs()
        .subscribe(message=>{
          // this.reloadFlag = 1-this.reloadFlag;
          this.messageService.new_alert(message.status_code, message.message);

          this.devDetail.getDevs();
        })
    }
  }

  // getDevDocs(): void {
  //   this.fileService.getAll(0)
  //     .subscribe(docs => this.devDocs = docs)
  // }

  // getProcessingFiles(): void {
  //   console.log("refresh!");
  //   this.fileService.getProcessingFiles(0)
  //     .subscribe(data=> {
  //       this.processingDocs = data;
  //       // console.log(data)
  //       this.fileService.close_alert();
  //       if (data.length != 0) {
  //         console.log("still got data");
  //         // this.isProcessingDevDocs = true;
  //         this.fileService.newAlerts(data);
  //       }
  //       // else {
  //       //   // this.isProcessingDevDocs = false;
  //       //   // this.devDetail.getDevs();
  //       //   // this.devDetail.ngOnInit();
  //       //
  //       //   console.log(this.devDetail.devs)
  //       // }
  //     })
  // }

  // getTypes(): void {
  //   this.typeService.getTypes()
  //     .subscribe(types => this.types = types)
  // }
  //
  // getParams(): void {
  //   this.paramService.getParameters()
  //     .subscribe(params=>{
  //       this.defaultThreshold = params[0].value;
  //     })
  // }

  // newMessageRefresh(): void {
  //   this.messageService.status$.subscribe(
  //     // res = alert_new
  //     res => {
  //       if (res) {
  //         console.log("delete all devs");
  //         this.devDetail.getDevs()
  //         //
  //         // setTimeout(()=>{
  //         //   this.devDetail.getDevs()
  //         // }, 2000
  //         // )
  //
  //         // this.getProcessingFiles();
  //       }
  //     }
  //   )
  // }

  // overallIntervalRefresh(): void {
  //   this.messageService.devEmitor$.subscribe(
  //     res => {
  //       console.log("get dev response from message service");
  //       if (res && !this.messageService.devStart) {
  //         console.log("start to process dev");
  //         this.messageService.devStart = true;
  //         // this.subscribe
  //         this.subscribe = this.source.subscribe( val => {
  //           this.testTime++;
  //           console.log("get all dev file status");
  //           this.getProcessingFiles();
  //           // this.devDetail.getDevs();
  //           if (this.processingDocs.length != 0) {
  //             console.log("still processing...");
  //           }
  //           else {
  //               console.log("processing dev finished!");
  //             // this.devDetail.getDevs();
  //             //   this.getProcessingFiles();
  //             console.log(this.devDetail);
  //               this.messageService.devStart = false;
  //               this.devDetail.getDevs();
  //               this.subscribe.unsubscribe();
  //
  //             // setTimeout(()=>{
  //             //   console.log("processing dev finished!");
  //             //   // this.getProcessingFiles();
  //             //   this.messageService.devStart = false;
  //             //   this.subscribe.unsubscribe();
  //             // }, 2000)
  //           }
  //         })
  //       }
  //       else if (this.messageService.devStart) {
  //         console.log("the dev processing has started!")
  //       }
  //     }
  //   )
  // }
}
