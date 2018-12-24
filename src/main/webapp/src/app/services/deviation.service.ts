import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import { Observable, of } from 'rxjs';
import {Deviation} from "../Deviation";


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'multipart/form-data' })
};

@Injectable({
  providedIn: 'root'
})
export class DeviationService {
  private api = 'http://localhost:7890/api/deviation/';  // URL to web api
  constructor(
    private http:HttpClient
  ) { }

  getDevs():Observable<Deviation[]> {
    return this.http.get<Deviation[]>(this.api+'getDevs');
  }
}
