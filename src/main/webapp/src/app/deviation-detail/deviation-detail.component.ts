import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {FileService} from "../services/file.service";
import {Deviation} from "../Deviation";
import {DeviationService} from "../services/deviation.service";

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

  constructor(private route: ActivatedRoute, private fileService: FileService, private deviationService: DeviationService) {
    // this.getDevs();
    // route.params.subscribe(params => {
      // this.typeId = +params['type'];
      // this.getDevs();
    // }, )
  }

  ngOnInit(): void {
    // this.typeId = +this.route.snapshot.paramMap.get('type');
    this.getDevs();
    this.fileService.getProcessingFiles(0)
      .subscribe(data=> {
        this.fileService.close_alert();
        if (data.length != 0) {
          this.fileService.newAlerts(data);
        }
      })
  }

  // ngOnChanges(): void {
    // this.getDevs();
  // }

  getDevs(): void {
    this.deviationService.getDevs()
      .subscribe(devs => this.devs = devs);
  }
}
