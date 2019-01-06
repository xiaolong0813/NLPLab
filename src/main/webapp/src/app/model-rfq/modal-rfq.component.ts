import {Component} from "@angular/core";

import { BsModalRef } from "ngx-bootstrap";

import { MessageService } from "../services/message.service";
import { FileService  } from "../services/file.service";
import {ParameterService} from "../services/parameter.service";

@Component({
  selector: 'modal-rfq',
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
            <label class="col-form-label">ZPF Model:
              <select [value]="selectedModel" #ref (change)="changeModel(ref.value)">
                <option [value]="item.id"
                        *ngFor="let item of ZPFModelArray">{{item.value}}</option>
              </select>
            </label>
          </div>

          <div class="form-group">
            <label class="col-form-label">RFQ var:
              <select [value]="selectedRfqVar" #ref (change)="changeRfqVar(ref.value)">
                <option [value]="item.id"
                        *ngFor="let item of RfqVarArray">{{item.value}}</option>
              </select>
            </label>
          </div>

          <div class="form-group">
            <label class="col-form-label">Sentence Similarity Algorithm:
              <select [value]="selectedSimilarityAlgo" #ref (change)="changeSimilarityAlgo(ref.value)">
                <option [value]="item.id"
                        *ngFor="let item of similarityAlgorithmArray">{{item.value}}</option>
              </select>
            </label>
          </div>

          <div class="form-group">
            <label class="col-form-label">Match Level:
              <select [value]="selectedLevel" #ref (change)="changeLevel(ref.value)">
                <option [value]="item.id"
                        *ngFor="let item of compareLevel">{{item.value}}</option>
              </select>
            </label>
          </div>
          <!--<input type="hidden" value="{{ doc_id }}">-->
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
        <button type="button" class="btn btn-primary" (click)="process()">Submit</button>
      </div>
    `
})

export class ModalRfqComponent {
  title: string;
  doc_id: number;
  similarityAlgorithmArray: object;
  ZPFModelArray: object;
  RfqVarArray: object;
  compareLevel: object;

  selectedRfqVar: number;
  selectedModel: number;
  selectedSimilarityAlgo: number;
  selectedLevel: number;
  // defaultSimilarityAlgo: number;

  constructor(public modalRef: BsModalRef, private parameterService: ParameterService, private messageService: MessageService, private fileService: FileService) {}

  closeModal() {
    // this.selectedModel = this.selectedRfqVar = 0;
    // this.selectedSimilarityAlgo = this.defaultSimilarityAlgo;
    this.modalRef.hide();
  }

  changeModel(value) {
    this.selectedModel = value;
  }

  changeRfqVar(value) {
    this.selectedRfqVar = value;
  }

  changeSimilarityAlgo(value) {
    this.selectedSimilarityAlgo = value;
  }

  changeLevel(value) {
    this.selectedLevel = value;
  }

  process() {
    this.fileService.processDoc(this.doc_id, this.selectedModel, this.selectedRfqVar, this.selectedSimilarityAlgo, this.selectedLevel)
      .subscribe(message => {
        if (message.status_code == 200) {
          location.reload();
        }
      });
  }
}
