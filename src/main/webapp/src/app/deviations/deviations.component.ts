import {Component, OnInit} from '@angular/core';

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

@Component({
  selector: 'app-deviations',
  templateUrl: './deviations.component.html',
  styleUrls: [ './deviations.component.scss' ]
})
export class DeviationsComponent implements OnInit{
  types: Type[];
  // reloadFlag: number;
  // defaultThreshold: number;

  modalRef: BsModalRef;
  constructor(private modalService: BsModalService, private deviationService: DeviationService, private messageService: MessageService) {}

  ngOnInit() {
    // this.reloadFlag = 1;
    // this.getTypes();
    // this.getParams();
  }

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
          window.location.reload(true)
        })
    }
  }
}
