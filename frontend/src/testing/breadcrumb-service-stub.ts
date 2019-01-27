import { Subject } from 'rxjs';
import { IBreadcrumbs } from 'src/app/breadcrumb.service';

export class BreadcrumbServiceStub {
    public breadcrumbs: Subject<IBreadcrumbs[]>;
}
