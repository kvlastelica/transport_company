import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IRouteSchedule } from '../route-schedule.model';
import { RouteScheduleService } from '../service/route-schedule.service';

@Component({
  templateUrl: './route-schedule-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class RouteScheduleDeleteDialogComponent {
  routeSchedule?: IRouteSchedule;

  protected routeScheduleService = inject(RouteScheduleService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.routeScheduleService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
