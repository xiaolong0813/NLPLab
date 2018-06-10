import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-deviation-detail',
  templateUrl: './deviation-detail.component.html',
  styleUrls: [ './deviation-detail.component.scss' ]
})
export class DeviationDetailComponent implements OnInit{
  public typeId: number;

  constructor(private route: ActivatedRoute) {
    route.params.subscribe(params => {
      this.typeId = +params['type']
    }, )
  }

  ngOnInit(): void {
    this.typeId = +this.route.snapshot.paramMap.get('type');
  }
}
