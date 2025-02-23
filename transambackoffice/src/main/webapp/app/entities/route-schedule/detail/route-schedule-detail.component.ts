import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IRouteSchedule } from '../route-schedule.model';

@Component({
  selector: 'jhi-route-schedule-detail',
  templateUrl: './route-schedule-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class RouteScheduleDetailComponent {
  routeSchedule = input<IRouteSchedule | null>(null);

  previousState(): void {
    window.history.back();
  }
}
