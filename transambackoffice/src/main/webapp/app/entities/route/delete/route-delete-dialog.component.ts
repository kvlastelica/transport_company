import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IRoute } from '../route.model';
import { RouteService } from '../service/route.service';

@Component({
  templateUrl: './route-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class RouteDeleteDialogComponent {
  route?: IRoute;

  protected routeService = inject(RouteService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.routeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
