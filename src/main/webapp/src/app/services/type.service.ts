import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";

import { Observable, of } from 'rxjs';

import { Type } from "../type";
import { Message } from "../message";
import {environment} from "../../environments/environment";

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({providedIn: 'root'})

export class TypeService {
  private api = environment.apiBase + '/api/types';  // URL to web api

  constructor(
    private http:HttpClient
  ) { }

  getTypes(): Observable<Type[]> {
    return this.http.get<Type[]>(this.api);
  }

  addType(type: Type): Observable<Message> {
    return this.http.post<Message>(this.api, type, httpOptions);
  }

  updateType(type: Type): Observable<Message> {
    return this.http.put<Message>(this.api, type, httpOptions);
  }

  deleteType(type: Type | number): Observable<Message> {
    const id = typeof type === 'number' ? type : type.id;
    const url = `${this.api}/delete/${id}`;

    return this.http.delete<Message>(url, httpOptions);
  }
}
