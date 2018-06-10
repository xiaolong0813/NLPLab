import {Component} from "@angular/core";

import { BsModalRef } from "ngx-bootstrap";

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
        <form>
          <div class="form-group">
            <label class="col-form-label">File:</label>
            <input type="file" class="form-control-file">
          </div>
          <div class="form-group">
            <label class="col-form-label">Type:</label>
            <ng-select [items]="types" bindLabel="name" bindValue="id" placeholder="Select type" class="form-control">
              <!--<ng-template ng-label-tmp let-item="item">-->
                <!--{{ item.name }}-->
              <!--</ng-template>-->
            </ng-select>
          </div>
          <div *ngIf="thresholdName != 'Group threshold'"  class="form-group">
            <label class="col-form-label">{{ thresholdName }}: {{ defaultThreshold }}</label>
            <input type="range" class="custom-range" min="0" max="1" step="0.1" value="{{ defaultThreshold }}" #ref (change)="changeThreshold(ref.value)">
          </div>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" (click)="modalRef.hide()">Close</button>
        <button type="button" class="btn btn-primary">Send message</button>
      </div>
    `
})

export class ModalContentComponent {
  title: string;
  thresholdName: string;
  defaultThreshold = 0.8;

  constructor(public modalRef: BsModalRef) {}

  changeThreshold(value: number) {
    this.defaultThreshold = value;
  }
}
