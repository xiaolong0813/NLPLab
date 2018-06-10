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
  groupThreshold: Parameter;
  matchupThreshold: Parameter;
  types: Type[];

  constructor(private typeService: TypeService, private paramService:ParameterService, private messageService: MessageService){}

  ngOnInit() {
    // this.groupThreshold = 0.1;
    // this.matchupThreshold = 0.1;
    this.getTypes();
    this.getParams();
  }

  getTypes(): void {
    this.typeService.getTypes()
      .subscribe(types => this.types = types)
  }

  addType(typeName: string): void {
    typeName = typeName.trim();
    if (!typeName) { return; }

    let new_type = {
      "name": typeName
    };
    this.typeService.addType(new_type as Type)
      .subscribe(message => {
        this.types = message.data as Type[];
        this.messageService.new_alert(message.status_code, message.message);
      }
    )
  }

  deleteType(typeId: number): void {
    if (confirm("Are you sure? This operation will remove all the relevant deviation records.")) {
      this.typeService.deleteType(typeId)
        .subscribe(message=>{
          this.types = message.data as Type[];
          this.messageService.new_alert(message.status_code, message.message);
        })
    }
  }

  getParams():void {
    this.paramService.getParameters()
      .subscribe(params=>{
        this.groupThreshold = params[0];
        this.matchupThreshold = params[1];
      })
  }

  changeGroupThreshold(value: number) {
    this.groupThreshold.value = value;
    this.paramService.updateParameter(this.groupThreshold)
      .subscribe(message=>{
        this.groupThreshold = message.data[0] as Parameter;
        this.messageService.new_alert(message.status_code, message.message);
      })
  }

  changeMatchupThreshold(value: number) {
    this.matchupThreshold.value = value;
    this.paramService.updateParameter(this.matchupThreshold)
      .subscribe(message=>{
        this.matchupThreshold = message.data[1] as Parameter;
        this.messageService.new_alert(message.status_code, message.message);
      })
  }
}
