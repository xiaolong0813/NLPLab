import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";

/*Observable是RxJS库中一个关键类，返回可观察对象版本的HeroService，
这里使用of()函数模拟从服务器返回数据*/

import { Observable, of } from 'rxjs';
import {Deviation} from "../Deviation";
import {Message} from "../message";
import {environment} from "../../environments/environment";

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'multipart/form-data' })
};

/*这里引入了新的装饰器@Injectable()，把后面相邻的类标记为依赖注入的参与者之一*/

/*providedIn用来创建和交付服务数据，用依赖注入系统。他会对
HeroService类实例化。@Injectable装饰器为服务创建提供商，这里
指定该服务在根注册器中提供，也可以规定在特定的模块@NgModule中
提供，比如providedIn: UserModule,*/

@Injectable({
  providedIn: 'root'
})

/*服务器控制器HeroService可以从任何地方获取数据，web，本地Local或模拟数据源*/
/*这样可以从组件中移除数据访问，不需要了解服务的内部实现*/
/*这里需要注入器，即一个对象，来选取和注入提供商*/

export class DeviationService {
  /*服务器上所请求的url*/
  private api = environment.apiBase + '/api/deviation/';  // URL to web api
  constructor(
    private http:HttpClient
  ) { }

  getDevs():Observable<Deviation[]> {
    return this.http.get<Deviation[]>(this.api+'getDevs');
  }

  removeAllDevs(): Observable<Message> {
    return this.http.delete<Message>(this.api)
  }
}
