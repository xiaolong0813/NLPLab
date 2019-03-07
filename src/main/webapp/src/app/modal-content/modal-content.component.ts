import {Component} from "@angular/core";

import { BsModalRef } from "ngx-bootstrap";

import { MessageService } from "../services/message.service";
import { FileService  } from "../services/file.service";

@Component({
  selector: 'modal-content',
  template: `
      <div class="modal-header">
        <h4 class="modal-title pull-left">{{ title }}</h4>
        <button type="button" class="close pull-right" aria-label="Close" (click)="modalRef.hide()">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <div>
          <div class="form-group">
            <label class="col-form-label">File:</label>
            <input type="file" class="form-control-file" (change)="handleFileInput($event.target.files)">
          </div>
          <!--<div class="form-group">-->
            <!--<label class="col-form-label">Type:</label>-->
            <!--<ng-select [items]="types"-->
                       <!--bindLabel="name"-->
                       <!--bindValue="id"-->
                       <!--placeholder="Select Type"-->
                       <!--[(ngModel)]="selectedTypeId"-->
                       <!--class="form-control">-->
            <!--</ng-select>-->
          <!--</div>-->
          <!--<div *ngIf="thresholdName != 'Group threshold'"  class="form-group">-->
            <!--<label class="col-form-label">{{ thresholdName }}: {{ defaultThreshold }}</label>-->
            <!--<input type="range" class="custom-range" min="0" max="1" step="0.1" value="{{ defaultThreshold }}" [(ngModel)]="defaultThreshold">-->
          <!--</div>-->
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" (click)="closeModal()">Close</button>
        <button type="button" class="btn btn-primary" (click)="uploadFiles()">Submit</button>
      </div>
    `
})

export class ModalContentComponent {
  title: string;
  filetype: string;
  // thresholdName: string;
  // defaultThreshold = 0.8;
  fileToUpload: File = null;
  formData: FormData = new FormData();
  // selectedTypeId: number = 0;

  constructor(public modalRef: BsModalRef, private messageService: MessageService, private fileService: FileService) {}

  closeModal() {
    this.formData = new FormData();
    this.fileToUpload = null;
    // this.selectedTypeId = 0;
    this.modalRef.hide();
  }

  handleFileInput(files: FileList) {
    this.fileToUpload = files.item(0);
  }

  uploadFiles() {
    if (this.fileToUpload == null) {
      this.messageService.new_alert(-1, "Please select file first.");
      return;
    }
    // if (this.selectedTypeId == 0 || this.selectedTypeId == null) {
    //   this.messageService.new_alert(-1, "Please select file type.");
    //   return;
    // }
    if (this.filetype == 'deviation') {
      //  Upload deviation file
      if (this.fileToUpload.type != "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") {
        this.messageService.new_alert(-1, "Please upload a file with .xlsx extension.");
        return;
      }
      this.formData.append("file", this.fileToUpload, this.fileToUpload.name);
      this.formData.append("fileType", "0");
      // this.formData.append("type", this.selectedTypeId.toString());
      // this.formData.append("threshold", this.defaultThreshold.toString());

      this.fileService.uploadFiles(this.formData)
        .subscribe(message=> {
          // this.messageService.new_alert(message.status_code, message.message);
          if (message.status_code == 200) {
            this.messageService.new_alert(message.status_code, message.message);
            location.reload();
            // this.closeModal();
          }
          // if (message.data != null) {
          //   this.fileService.newAlerts(message.data);
          // }
        });
    } else if (this.filetype == 'deviation_src') {
      //  Upload deviation source file
      if (this.fileToUpload.type != "application/vnd.openxmlformats-officedocument.wordprocessingml.document") {
        this.messageService.new_alert(-1, "Please upload a file with .docx extension.");
        return;
      }
      this.formData.append("file", this.fileToUpload, this.fileToUpload.name);
      this.formData.append("fileType", "2");
      // this.formData.append("type", this.selectedTypeId.toString());
      // this.formData.append("threshold", this.defaultThreshold.toString());
      this.fileService.uploadFiles(this.formData)
        .subscribe(message=> {
          if (message.status_code == 200) {
            this.messageService.new_alert(message.status_code, message.message);
            location.reload();
            // this.messageService.new_alert(message.status_code, message.message);
            // this.closeModal();
          }
          // if (message.data.length != 0) {
          //   this.fileService.newAlerts(message.data);
          // }
        });

    } else if (this.filetype == 'rfq') {
      //  Upload RFQ file
      if (this.fileToUpload.type != "application/vnd.openxmlformats-officedocument.wordprocessingml.document") {
        this.messageService.new_alert(-1, "Please upload a file with .docx extension.");
        return;
      }
      this.formData.append("file", this.fileToUpload, this.fileToUpload.name);
      this.formData.append("fileType", "1");
      // this.formData.append("type", this.selectedTypeId.toString());
      // this.formData.append("threshold", this.defaultThreshold.toString());
      this.fileService.uploadFiles(this.formData)
        .subscribe(message=> {
          if (message.status_code == 200) {
            this.messageService.new_alert(message.status_code, message.message);
            location.reload();
          }
        });
    }
  }
}
