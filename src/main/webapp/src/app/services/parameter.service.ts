import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";

import { Observable, of } from 'rxjs';

import { Parameter } from "../parameter";
import { Message } from "../message";

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class ParameterService {
  private api = 'http://localhost:8080/api/parameters';  // URL to web api
  constructor(
    private http: HttpClient
  ) { }

  getParameters(): Observable<Parameter[]> {
    return this.http.get<Parameter[]>(this.api);
  }
  updateParameter(parameter: Parameter): Observable<Message> {
    return this.http.put<Message>(this.api, parameter, httpOptions);
  }
}
