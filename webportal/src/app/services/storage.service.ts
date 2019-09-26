import { Injectable, Inject } from '@angular/core';
import { SESSION_STORAGE, StorageService } from 'ngx-webstorage-service';
import { Condition } from '../entities/conditions/condition';

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
}
