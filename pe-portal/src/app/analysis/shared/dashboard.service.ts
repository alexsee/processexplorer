import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EventLogDashboard } from 'src/app/log/models/eventlog-dashboard.model';
import { environment } from 'src/environments/environment';
import { WidgetChartComponent } from '../components/chart/chart.component';
import { WidgetProcessMapComponent } from '../components/process-map/process-map.component';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  constructor(
    private httpClient: HttpClient
  ) { }

  getDashboards(logName: string): Observable<number[]> {
    return this.httpClient.get<number[]>(environment.serviceUrl + '/dashboard/logName?logName=' + logName);
  }

  getDashboard(id: number): Observable<EventLogDashboard> {
    return this.httpClient.get<EventLogDashboard>(environment.serviceUrl + '/dashboard?id=' + id);
  }

  save(dashboard: EventLogDashboard): Observable<EventLogDashboard> {
    return this.httpClient.post<EventLogDashboard>(environment.serviceUrl + '/dashboard', dashboard);
  }

  parseDashboard(content: string): any {
    const obj: any = JSON.parse(content);

    for (const widget of obj) {
      const typename = widget.widget.typename;

      if (typename === 'WidgetChartComponent') {
        widget.widget.type = WidgetChartComponent;
      } else if (typename === 'WidgetProcessMapComponent') {
        widget.widget.type = WidgetProcessMapComponent;
      }
    }

    return obj;
  }

  stringifyDashboard(dashboard: any): string {
    for (const widget of dashboard) {
      const type = widget.widget.type;
      widget.widget.typename = type.name;
    }

    return JSON.stringify(dashboard);
  }
}
