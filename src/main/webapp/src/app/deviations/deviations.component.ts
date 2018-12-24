import {Component, OnInit} from '@angular/core';

import { BsModalService } from "ngx-bootstrap";
import { BsModalRef } from "ngx-bootstrap";
import { ModalContentComponent } from "../modal-content/modal-content.component";

import { Type } from "../type";
import { Parameter } from "../parameter";

import { TypeService } from "../services/type.service";
import { ParameterService } from "../services/parameter.service";

@Component({
  selector: 'app-deviations',
  templateUrl: './deviations.component.html',
  styleUrls: [ './deviations.component.scss' ]
})
export class DeviationsComponent implements OnInit{
  types: Type[];
  // defaultThreshold: number;

  modalRef: BsModalRef;
  constructor(private modalService: BsModalService, private typeService: TypeService, private paramService: ParameterService) {}

  ngOnInit() {
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

  openModelWithComponent() {
    const initialState = {
      title: 'Upload deviation file',
      filetype: 'deviation'
      // thresholdName: 'Group threshold',
      // defaultThreshold: this.defaultThreshold,
      // types: this.types
    };
    this.modalRef = this.modalService.show(ModalContentComponent, {initialState});
  }
}
