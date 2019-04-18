import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {FileService} from "../services/file.service";
import {Deviation} from "../Deviation";
import {DeviationService} from "../services/deviation.service";
import {MessageService} from "../services/message.service";
import {interval, Subscription} from "rxjs";

import {ChangeDetectorRef} from "@angular/core";

@Component({
  selector: 'app-deviation-detail',
  templateUrl: './deviation-detail.component.html',
  styleUrls: [ './deviation-detail.component.scss' ],
  // changeDetection: ChangeDetectionStrategy.OnPush
})
export class DeviationDetailComponent implements OnInit{
  // @Input() reload: number;
  // public typeId: number;
  public devs: Deviation[];

  constructor(private route: ActivatedRoute, private fileService: FileService, private deviationService: DeviationService,
              private changeDetector : ChangeDetectorRef) {
    // this.getDevs();
    // route.params.subscribe(params => {
      // this.typeId = +params['type'];
      // this.getDevs();
    // }, )
  }

  ngOnInit(): void {
    // this.getDevs();
    // this.typeId = +this.route.snapshot.paramMap.get('type');
    // this.getDevs();
    // this.getProcessingFiles();

    // this.newMessageRefresh();
    // this.overallIntervalRefresh();
  }

  // ngOnChanges(): void {
    // this.getDevs();
  // }

  getDevs(): void {
    console.log("get all devs!");
    this.deviationService.getDevs()
      .subscribe(devs => {
        this.devs = devs
      });
  }
}
