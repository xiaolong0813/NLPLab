import { Component, OnInit } from '@angular/core';

import { BsModalService } from "ngx-bootstrap";
import { BsModalRef } from "ngx-bootstrap";
import { ModalContentComponent } from "../modal-content/modal-content.component";

import { Type } from "../type";
import { Parameter } from "../parameter";

import { TypeService } from "../services/type.service";
import { ParameterService } from "../services/parameter.service";

@Component({
  selector: 'app-rfq',
  templateUrl: './rfq.component.html',
  styleUrls: [ './rfq.component.scss' ]
})
export class RfqComponent implements OnInit{
  types: Type[];
  defaultThreshold: number;

  modalRef: BsModalRef;
  constructor(private modalService: BsModalService, private typeService: TypeService, private paramService: ParameterService) {}

  ngOnInit() {
    this.getTypes();
    this.getParams();
  }

  getTypes(): void {
    this.typeService.getTypes()
      .subscribe(types => this.types = types)
  }

  getParams(): void {
    this.paramService.getParameters()
      .subscribe(params=>{
        this.defaultThreshold = params[1].value;
      })
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
}
