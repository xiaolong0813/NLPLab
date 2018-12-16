import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpResponse} from "@angular/common/http";

import { Observable, of } from 'rxjs';

import { Message } from "../message";
import {Document} from "../document";
import {tap} from "rxjs/operators";

const httpOptions = {
  headers: {'Accept': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'},
  responseType: 'blob'
};

@Injectable({providedIn: 'root'})

export class FileService {
  private api = 'http://localhost:7890/api/file/';  // URL to web api
  processedFile: Document[];
  alert_new = "none";

  constructor(
    private http:HttpClient
  ) { }

  uploadFiles(formData: FormData): Observable<Message> {
    return this.http.post<Message>(this.api+'upload', formData);
  }

  getProcessingFiles(fileType: number): Observable<Document[]> {
    return this.http.get<Document[]>(this.api+'getProcessing/'+fileType);
  }

  getAll(fileType: number): Observable<Document[]> {
    return this.http.get<Document[]>(this.api+'getAll/'+fileType);
  }

  processDoc(doc_id: number): Observable<Message> {
    return this.http.get<Message>(this.api+'processDoc/'+doc_id);
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

  newAlerts(docs: Document[]) {
    this.alert_new = "";
    this.processedFile = docs;
    // setTimeout(()=> {
    //   this.alert_new = "None";
    // }, 5000)
  }

  close_alert() {
    this.alert_new = "none";
  }
}
