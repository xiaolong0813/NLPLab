import { Component, OnInit } from '@angular/core';
import { XmlTagContent } from "../XmlTagContent";
import {FileService} from "../services/file.service";
import {TranslationService} from "../services/translation.service";


@Component({
  selector: 'app-translation-detail',
  templateUrl: './translation-detail.component.html',
  styleUrls: ['./translation-detail.component.scss']
})
export class TranslationDetailComponent implements OnInit {

  public xmltag: XmlTagContent[];
  // public test;
  public display = "none";

  constructor(
    private fileService: FileService,
    private transService: TranslationService
    ) { }

  ngOnInit() {

  }

  // test(xml_id:number) {
  //   console.log("the area refreshed!: " + xml_id);
  //   location.reload()
  // }

  getTranslation(xml_id: number) {
    this.display = "";
    this.transService.getTranslation(xml_id)
      .subscribe(trans => {
        this.xmltag = trans;
        console.log(trans)
      });
  }
}
