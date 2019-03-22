import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams, HttpResponse} from "@angular/common/http";

import { Observable, of } from 'rxjs';

import { Message } from "../message";
import {Document} from "../document";
import {tap} from "rxjs/operators";
import {Xmls} from "../xmls";

const httpOptions = {
  headers: {'Accept': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'},
  responseType: 'blob'
};

@Injectable({providedIn: 'root'})

export class FileService {
  private api = 'http://localhost:7890/api/file/';  // URL to web api
  processedFile: Document[];
  processedXML: Xmls[];
  alert_new = "none";

  constructor(
    private http:HttpClient
  ) { }

  // testurl(xml_id):Observable<Message> {
  //   return this.http.get<Message>(this.api + 'testurl/', {params:{'xml_id' : xml_id.toString()}});
  // }

  uploadFiles(formData: FormData): Observable<Message> {
    return this.http.post<Message>(this.api + 'upload', formData);
  }

  getProcessingFiles(fileType: number): Observable<Document[]> {
    return this.http.get<Document[]>(this.api+'getProcessing/'+fileType);
  }

  getAll(fileType: number): Observable<Document[]> {
    return this.http.get<Document[]>(this.api+'getAll/'+fileType);
  }

  getAllXMLs(): Observable<Xmls[]> {
    return this.http.get<Xmls[]>(this.api + 'getAllXMLs');
  }

  removeAllDOC(): Observable<Message> {
    return this.http.delete<Message>(this.api)
  }

  removeAllXmls(): Observable<Message> {
    return this.http.delete<Message>(this.api + 'allXmls')
  }

  processDoc(doc_id: number, model: number, rfqvar: number, simalgo: number, level:number): Observable<Message> {
    return this.http.get<Message>(this.api+'processDoc/', {
      params: {
        'doc_id': doc_id.toString(),
        'model': model.toString(),
        'rfqvar': rfqvar.toString(),
        'simalgo': simalgo.toString(),
        'level': level.toString()
      }
    });
  }

  downloadXlsx(doc_id: number): Observable<Blob> {
    return this.http.get(this.api+'downloadXlsx/'+doc_id, {
      headers: {'Accept': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'},
      responseType: 'blob'
    }).pipe(
      tap(
        data => console.log(data),
        error=> console.log(error)
      )
    );
  }

  downloadDocx(doc_id: number): Observable<Blob> {
    return this.http.get(this.api+'downloadDocx/'+doc_id, {
      headers: {'Accept': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'},
      responseType: 'blob'
    }).pipe(
      tap(
        data => console.log(data),
        error=> console.log(error)
      )
    );
  }

  downloadXML(xml_id: number): Observable<Blob> {
    return this.http.get(this.api + 'downloadXml/' + xml_id, {
      headers: {'Accept': "text/xml"},
      responseType: 'blob'
    }).pipe(
      tap(
        data => console.log(data),
        error => console.log(error)
      )
    )
  }

  newAlerts(docs: Document[]) {
    this.alert_new = "";
    this.processedFile = docs;
    // setTimeout(()=> {
    //   this.alert_new = "None";
    // }, 5000)
  }

  newXMLAlerts(xmls: Xmls[]) {
    this.alert_new = "";
    this.processedXML = xmls
  }

  close_alert() {
    this.alert_new = "none";
  }

}
