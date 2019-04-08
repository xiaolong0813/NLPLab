import { Injectable } from '@angular/core';
import {interval, Subject, Subscription} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  alert_new = false;

  // observer for rfq
  // public processing;
  public rfqStart = false;
  public rfqEmitor = new Subject();
  public rfqEmitor$ = this.rfqEmitor.asObservable();

  // source 为被观察者对象Subject
  // status$ 为source的订阅者

  private source = new Subject<any>();
  public status$ = this.source.asObservable();

  alert_type = "success";
  alert_message = "This is a message.";

  new_alert(code: number, message: string) {
    switch (code) {
      case 200: {
        this.alert_type = "success";
        break;
      }
      case -1: {
        this.alert_type = "danger";
        break;
      }
      default: {
        break;
      }
    }
    this.alert_message = message;
    this.alert_new = true;

    // 将aler_new作为返回值传给订阅者的next函数=>
    this.source.next(this.alert_new);

    setTimeout(()=> {
      this.alert_new = false;
    }, 5000)
  }

  close_alert() {
    this.alert_new = false;
  }

}
