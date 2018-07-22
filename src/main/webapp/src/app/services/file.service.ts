import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpResponse} from "@angular/common/http";

import { Observable, of } from 'rxjs';

import { Message } from "../message";
import {Document} from "../document";

const httpOptions = {
  headers: {'Accept': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'}
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

  downloadDoc(doc_id: number): Observable<Blob> {
    return this.http.get<Blob>(this.api+'downloadDoc/'+doc_id, httpOptions);
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
