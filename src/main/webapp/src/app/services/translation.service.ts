import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {XmlTagContent} from "../XmlTagContent";
import {Message} from "../message";


const httpOptions = {
  // headers: new HttpHeaders({'Content-Type': 'multipart/form-data'})
  headers: {'Accept': "text/xml"},
  responseType: 'blob'
};

@Injectable({
  providedIn: 'root'
})
export class TranslationService {

  private api = 'http://localhost:7890/api/translation/';

  constructor(
    private http: HttpClient
  ) { }

  processXml(xml_id):Observable<Message> {
    return this.http.get<Message>(this.api + 'processXml/', {params:{'xml_id' : xml_id.toString()}});
  }

  testurl(xml_id):Observable<Message> {
    return this.http.get<Message>(this.api + 'testurl/', {params:{'xml_id' : xml_id.toString()}});
  }



  // getTagContent(xml_id: number):Observable<> {
  //
  // }


  getTranslation(xml_id : number): Observable<XmlTagContent[]> {
    return this.http.get<XmlTagContent[]>(this.api + 'getTags/' + xml_id);
  }
}
