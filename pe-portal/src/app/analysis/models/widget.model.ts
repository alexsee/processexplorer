import { Type } from '@angular/core';
import { WidgetComponent } from '../components/widget.component';

export class Widget {
    type: Type<WidgetComponent>;
    options: any;
}
