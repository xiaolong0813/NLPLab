import { Injectable } from '@angular/core';
import {Observable, Subject, Subscription} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class EmitorService {

  // // observer for rfq
  // // public processing;
  // public devStart = false;
  // public devEmitor = new Subject<any>();
  // public devEmitor$ = this.devEmitor.asObservable();
  //
  // // observer for rfq
  // // public processing;
  // public rfqStart = false;
  // public rfqEmitor = new Subject();
  // public rfqEmitor$ = this.rfqEmitor.asObservable();

  public transTimerStart: boolean;
  public rfqTimerStart: boolean;
  public devTimerStart: boolean;

  // 每个组件的被观察者对象
  public transEmitor = new Subject<any>();
  public devEmitor = new Subject<any>();
  public rfqEmitor = new Subject<any>();


  // 通知订阅者发送了事件
  emitEvent(emitor: Subject<any>, msg?: string) {
    if (msg) {
      emitor.next(msg)
    } else {
      emitor.next(true);
    }
  }

  // 清除事件
  clearEvent(emitor: Subject<any>) {
    emitor.next();
  }

  // 清除观察者
  clearObservors(emitor: Subject<any>) {
    emitor.observers = [];
  }

  // 获取所发送的事件
  getEvent(emitor: Subject<any>): Observable<any> {
    return emitor.asObservable();
  }







  constructor() {}
}
