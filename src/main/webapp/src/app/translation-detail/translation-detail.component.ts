import {Component, ElementRef, OnInit, Renderer2, ViewChild} from '@angular/core';
import { XmlTagContent } from "../XmlTagContent";
import {FileService} from "../services/file.service";
import {TranslationService} from "../services/translation.service";
import {id} from "@swimlane/ngx-datatable/release/utils";


@Component({
  selector: 'app-translation-detail',
  templateUrl: './translation-detail.component.html',
  styleUrls: ['./translation-detail.component.scss']
})
export class TranslationDetailComponent implements OnInit {

  public xmltag: XmlTagContent[];
  // public test;
  public display = "none";
  public xml_id : number;
  // private updateTag: string;

  constructor(
    private render2: Renderer2,
    private fileService: FileService,
    private transService: TranslationService
    ) { }

  ngOnInit() {
  }

  getTranslation() {
    this.display = "";
    this.transService.getTranslation(this.xml_id)
      .subscribe(trans => {
        this.xmltag = trans;
        this.xmltag.forEach(tag => {
          tag.status = 'uneditable'
        });
        // console.log("xml " + this.xml_id + " translation are list!");
      });
  }

  changeToEdit(tag : any) {
    // console.log("edit:" + tag)
    tag.status = 'editable';
    // console.log(tag)
  }

  saveUpdate(tag: any) {
    // tag.status = 'uneditable';
    tag.updateTranslation = this.render2.selectRootElement('.tag_' + tag.id).value;
    this.transService.updateTranslation(this.xml_id ,tag.id, tag.updateTranslation)
      .subscribe(mes => {
        if (mes.status_code == 200) {
          this.getTranslation();
          // console.log(mes.message);
        }
      });
  }

  deleteTranslation(tagId : number) {
    if (confirm("Are you sure to remove this translation? (Tag content would be kept Untranslated)"))  {
      this.transService.deleteTranslation(tagId)
        .subscribe(mes => {
          if (mes.status_code == 200) {
            console.log("tag " + tagId + " of xml " + this.xml_id + " is deleted successfully");
            this.getTranslation()
          }
        })
    }
  }

  cancelEdit(tag : any) {
    tag.status = 'uneditable';
    // console.log(tag)
  }
}