import { Injectable, Inject } from '@angular/core';
import { SESSION_STORAGE, StorageService } from 'ngx-webstorage-service';
import { Condition } from '../entities/conditions/condition';
import { Type } from '@angular/compiler';

@Injectable({
  providedIn: 'root'
})
export class LocalStorageService {

  constructor(@Inject(SESSION_STORAGE) private storage: StorageService) { }

  public readQueryConditions(logName: string): Condition[] {
    return this.storage.get(logName + '.queryConditions');
  }

  public writeQueryConditions(logName: string, conditions: Condition[]) {
    this.storage.set(logName + '.queryConditions', conditions);
  }

  public readConfig<T>(logName: string, setting: string): T {
    return this.storage.get(logName + '.' + setting);
  }

  public writeConfig<T>(logName: string, setting: string, value: T) {
    this.storage.set(logName + '.' + setting, value);
  }
}
